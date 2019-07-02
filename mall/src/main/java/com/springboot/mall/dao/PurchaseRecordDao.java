package com.springboot.mall.dao;

import com.springboot.mall.pojo.PurchaseRecord;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseRecordDao {
    int insertPurchaseRecord(PurchaseRecord purchaseRecord);
}
