package com.springboot.mall.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Alias("purchaseRecord")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseRecord implements Serializable {
    private Long id;
    private Long userId;
    private Long productId;
    private BigDecimal price;
    private int quantity;
    private BigDecimal sum;
    private LocalDateTime purchaseTime;
    private String note;
}
