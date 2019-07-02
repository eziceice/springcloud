package com.springboot.mall.service;

import com.springboot.mall.pojo.PurchaseRecord;

import java.util.List;

public interface PurchaseService {
    int purchase(Long userId, Long productId, int quantity);

    boolean purchaseRedis(Long userId, Long productId, int quantity);

    void dealRedisPurchase(List<PurchaseRecord> purchaseRecords);
}
