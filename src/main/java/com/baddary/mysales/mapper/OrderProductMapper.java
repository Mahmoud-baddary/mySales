package com.baddary.mysales.mapper;

import com.baddary.mysales.dto.OrderProductDTO;
import com.baddary.mysales.row.OrderProductPurchaseRow;
import com.baddary.mysales.row.OrderProductRow;
import com.baddary.mysales.row.OrderProductSaleRow;

public class OrderProductMapper {
    private OrderProductMapper(){}

    public static OrderProductDTO toDTO(OrderProductPurchaseRow row){
        OrderProductDTO dto = new OrderProductDTO();
        dto.setQuantity(row.getQuantity());
        dto.setNote(row.getNote());
        dto.setUnit(row.getUnit());
        dto.setProductId(row.getProductId());
        dto.setBatch(row.getBatch());
        dto.setDiscount(row.getDiscount());
        dto.setPrice(row.getPrice());
        dto.setExpireDate(row.getExpireDate());
        return dto;
    }
    public static OrderProductDTO toDTO(OrderProductSaleRow row){
        OrderProductDTO dto = new OrderProductDTO();
        dto.setQuantity(row.getQuantity());
        dto.setUnit(row.getUnit());
        dto.setNote(row.getNote());
        dto.setProductId(row.getProductId());
        dto.setBatch(row.getBatch());
        dto.setDiscount(row.getDiscount());
        dto.setPrice(row.getPrice());
        dto.setExpireDate(row.getExpireDate());
        return dto;
    }

    public static OrderProductRow toRow(OrderProductDTO dto){
        OrderProductRow row = new OrderProductRow();
        row.setBatch(dto.getBatch());
        row.setDiscount(dto.getDiscount());
        row.setExpireDate(dto.getExpireDate());
        row.setNote(dto.getNote());
        row.setPrice(dto.getPrice());
        row.setProductId(dto.getProductId());
        row.setQuantity(dto.getQuantity());
        row.setUnit(dto.getUnit());
        row.setOrderId(dto.getOrderId());
        row.setProductName(dto.getProductName());
        
        return row;
    }

   
}
