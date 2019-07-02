package com.springboot.mall.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.math.BigDecimal;

@Alias("product")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product implements Serializable {
    private Long id;
    private String productName;
    private int stock;
    private BigDecimal price;
    private int version;
    private String note;
}
