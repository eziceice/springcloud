package com.springboot.mall.dao;

import com.springboot.mall.pojo.Product;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductDao {

    Product getProduct(Long id);

    int decreaseProduct(@Param("id") Long id, @Param("quantity") int quantity, @Param("version") int version);

    int decreaseProductWithoutVersion(@Param("id") Long id, @Param("quantity") int quantity);
}
