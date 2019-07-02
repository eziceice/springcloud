package com.springboot.mall.service;

import com.springboot.mall.pojo.PurchaseRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

@Service
public class TaskServiceImpl implements TaskService {

    private static final String PRODUCT_SCHEDULE_SET = "product_schedule_set";
    private static final String PURCHASE_PRODUCT_LIST = "purchase_list_";
    private static final int ONE_TIME_SIZE = 1000;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private PurchaseService purchaseService;

    @Override
    @Scheduled(cron = "0 0 1 * * ?")
    public void purchaseTask() {
        System.out.println("Start scheduled task...");
        Set<String> productIdList = stringRedisTemplate.opsForSet().members(PRODUCT_SCHEDULE_SET);
        List<PurchaseRecord> purchaseRecords = new ArrayList<>();
        for (String productIdStr : productIdList) {
            Long productId = Long.parseLong(productIdStr);
            String purchaseKey = PURCHASE_PRODUCT_LIST + productId;
            BoundListOperations<String, String> ops = stringRedisTemplate.boundListOps(purchaseKey);
            long size = stringRedisTemplate.opsForList().size(purchaseKey);
            long times = size % ONE_TIME_SIZE == 0 ? size / ONE_TIME_SIZE : size / ONE_TIME_SIZE + 1;

            for (int i = 0; i < times; i++) {
                List<String> prList;
                if (i == 0) {
                    prList = ops.range(i * ONE_TIME_SIZE, (i + 1) * ONE_TIME_SIZE);
                } else {
                    prList = ops.range(i * ONE_TIME_SIZE + 1, (i + 1) * ONE_TIME_SIZE);
                }

                for (String prStr : prList) {
                    PurchaseRecord purchaseRecord = createPurchaseRecord(productId, prStr);
                    purchaseRecords.add(purchaseRecord);
                }

                try {
                    purchaseService.dealRedisPurchase(purchaseRecords);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            stringRedisTemplate.delete(purchaseKey);
            stringRedisTemplate.opsForSet().remove(PRODUCT_SCHEDULE_SET, productIdStr);
        }

        System.out.println("Finish scheduled task..");
    }

    private PurchaseRecord createPurchaseRecord(Long productId, String prStr) {
        String[] arr = prStr.split(",");
        LocalDateTime purchaseTime =
                LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.valueOf(arr[4])),
                        TimeZone.getDefault().toZoneId());
        return PurchaseRecord.builder().userId(Long.valueOf(arr[0]))
                .quantity(Integer.valueOf(arr[1]))
                .sum(new BigDecimal(arr[2]))
                .price(new BigDecimal(arr[3]))
                .purchaseTime(purchaseTime)
                .note("Purchase Time: " + purchaseTime).build();
    }
}
