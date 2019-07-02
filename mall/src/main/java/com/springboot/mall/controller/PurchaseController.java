package com.springboot.mall.controller;

import com.springboot.mall.dto.PurchaseRecordDTO;
import com.springboot.mall.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/purchases")
public class PurchaseController {

    @Autowired
    private PurchaseService purchaseService;

    @PostMapping
    public ResponseEntity<PurchaseRecordDTO> purchase(@RequestBody PurchaseRecordDTO purchaseRecordDTO)
    {
        Long userId = Long.valueOf(purchaseRecordDTO.getUserId());
        Long productId = Long.valueOf(purchaseRecordDTO.getProductId());
        int quantity = Integer.valueOf(purchaseRecordDTO.getQuantity());
        int stock = purchaseService.purchase(userId, productId, quantity);
        purchaseRecordDTO.setStock(String.valueOf(stock));
        return new ResponseEntity<>(purchaseRecordDTO, HttpStatus.CREATED);
    }

    @PostMapping(value = "/redis")
    public ResponseEntity<PurchaseRecordDTO> purchaseRedis(@RequestBody PurchaseRecordDTO purchaseRecordDTO) {
        Long userId = Long.valueOf(purchaseRecordDTO.getUserId());
        Long productId = Long.valueOf(purchaseRecordDTO.getProductId());
        int quantity = Integer.valueOf(purchaseRecordDTO.getQuantity());
        purchaseService.purchaseRedis(userId, productId, quantity);
        return new ResponseEntity<>(purchaseRecordDTO, HttpStatus.CREATED);
    }

}
