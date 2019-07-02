package com.springboot.mall.service;

import com.springboot.mall.dao.ProductDao;
import com.springboot.mall.dao.PurchaseRecordDao;
import com.springboot.mall.pojo.Product;
import com.springboot.mall.pojo.PurchaseRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PurchaseServiceImpl implements PurchaseService {

    @Autowired
    private ProductDao productDao;

    @Autowired
    private PurchaseRecordDao purchaseRecordDao;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    //Redis Lua Script -- 需要提前将数据库中的数据load到Redis中然后才能开始执行程序，否则无法获取stock
    private String purchaseScript = "redis.call('sadd', KEYS[1], ARGV[2]) \n"
            + "local productPurchaseList = KEYS[2]..ARGV[2] \n"
            + "local userId = ARGV[1] \n"
            + "local product = 'product_'..ARGV[2] \n"
            + "local quantity = tonumber(ARGV[3]) \n"
            + "local stock = tonumber(redis.call('hget', product, 'stock')) \n"
            + "local price = tonumber(redis.call('hget', product, 'price')) \n"
            + "local purchase_date = ARGV[4] \n"
            + "if stock < quantity then return 0 end \n"
            + "stock = stock - quantity \n"
            + "redis.call('hset', product, 'stock', tostring(stock)) \n"
            + "local sum = price * quantity \n"
            + "local purchaseRecord = userId..','..quantity..','"
            + "..sum..','..price..','..purchase_date \n"
            + "redis.call('rpush', productPurchaseList, purchaseRecord) \n"
            + "return 1 \n";

    private static final String PURCHASE_PRODUCT_LIST = "purchase_list_";

    private static final String PRODUCT_SCHEDULE_SET = "product_schedule_set";

    // SHA1 code, redis cache lua script and return sha1
    private String sha1 = null;


    // Save purchase records into redis
    public boolean purchaseRedis(Long userId, Long productId, int quantity) {
        long purchaseDate = System.currentTimeMillis();
        try (Jedis jedis = (Jedis) stringRedisTemplate.getConnectionFactory().getConnection().getNativeConnection()) {
            if (sha1 == null) {
                sha1 = jedis.scriptLoad(purchaseScript);
            }

            Object res = jedis.evalsha(sha1, 2, PRODUCT_SCHEDULE_SET, PURCHASE_PRODUCT_LIST,
                    userId + "", productId + "", quantity + "", purchaseDate + "");
            Long result = (Long) res;
            return result == 1;
        }
    }

    /**
     * Persist all redis purchase records into database
     *
     * @param purchaseRecords
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void dealRedisPurchase(List<PurchaseRecord> purchaseRecords) {
        for (PurchaseRecord purchaseRecord : purchaseRecords) {
            purchaseRecordDao.insertPurchaseRecord(purchaseRecord);
            productDao.decreaseProductWithoutVersion(purchaseRecord.getId(), purchaseRecord.getQuantity());
        }
    }

    /**
     * Optimistic Lock without Redo
     *
     * @param userId
     * @param productId
     * @param quantity
     * @return
     */
    @Transactional
    public int purchaseOptimisticLockNoRedo(Long userId, Long productId, int quantity) {
        Product product = productDao.getProduct(productId);
        if (product.getStock() < quantity) {
            return -1;
        }
        int version = product.getVersion();
        int result = productDao.decreaseProduct(productId, quantity, version);
        if (result == 0) {
            return -1;
        }
        PurchaseRecord purchaseRecord = PurchaseRecord.builder().note("Purchase Time: " + LocalDateTime.now().toString())
                .price(product.getPrice()).productId(productId).quantity(quantity)
                .sum(product.getPrice().multiply(new BigDecimal(quantity))).userId(userId).build();
        purchaseRecordDao.insertPurchaseRecord(purchaseRecord);
        return product.getStock() - quantity;
    }

    /**
     * Optimistic Lock with Redo (TimeStamp)
     *
     * @param userId
     * @param productId
     * @param quantity
     * @return
     */
    @Override
    @Transactional
    public int purchase(Long userId, Long productId, int quantity) {
        long start = System.currentTimeMillis();
        while (true) {
            long end = System.currentTimeMillis();
            if (end - start > 100) {
                return -1;
            }
            Product product = productDao.getProduct(productId);
            if (product.getStock() < quantity) {
                return -1;
            }
            int version = product.getVersion();
            int result = productDao.decreaseProduct(productId, quantity, version);
            if (result == 0) {
                continue;
            }
            PurchaseRecord purchaseRecord = PurchaseRecord.builder().note("Purchase Time: " + LocalDateTime.now().toString())
                    .price(product.getPrice()).productId(productId).quantity(quantity)
                    .sum(product.getPrice().multiply(new BigDecimal(quantity))).userId(userId).build();
            purchaseRecordDao.insertPurchaseRecord(purchaseRecord);
            return product.getStock() - quantity;
        }
    }

    /**
     * Optimistic Lock with Redo (Redo Times)
     *
     * @param userId
     * @param productId
     * @param quantity
     * @return
     */
    @Transactional
    public int purchaseOptimisticLockWithRedoTimes(Long userId, Long productId, int quantity) {
        for (int i = 0; i < 3; i++) {
            Product product = productDao.getProduct(productId);
            if (product.getStock() < quantity) {
                return -1;
            }
            int version = product.getVersion();
            int result = productDao.decreaseProduct(productId, quantity, version);
            if (result == 0) {
                continue;
            }
            PurchaseRecord purchaseRecord = PurchaseRecord.builder().note("Purchase Time: " + LocalDateTime.now().toString())
                    .price(product.getPrice()).productId(productId).quantity(quantity)
                    .sum(product.getPrice().multiply(new BigDecimal(quantity))).userId(userId).build();
            purchaseRecordDao.insertPurchaseRecord(purchaseRecord);
            return product.getStock() - quantity;
        }
        return -1;
    }


}
