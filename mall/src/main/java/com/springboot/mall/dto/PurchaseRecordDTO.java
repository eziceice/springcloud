package com.springboot.mall.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseRecordDTO {
    private String userId;
    private String productId;
    private String quantity;
    private String stock;
}
