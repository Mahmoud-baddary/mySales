package com.baddary.mysales.mapper;

import com.baddary.mysales.dto.OrderProductDTO;
import com.baddary.mysales.row.OrderProductPurchaseRow;
import com.baddary.mysales.row.OrderProductSaleRow;
import com.baddary.mysales.row.OrderProductSearchRow;

public class OrderProductMapper {
    private OrderProductMapper(){}

    // public static OrderProductSaleRow toSalesRow(OrderProductDTO dto){
    //     OrderProductSaleRow row = new OrderProductSaleRow();
    //     row.setBatch(dto.getBatch());
    //     row.setDiscount(dto.getDiscount());
    //     row.setExpireDate(dto.getExpireDate());
    //     row.setNote(dto.getNote());
    //     row.setUnit(dto.getUnit());
    //     row.setQuantity(dto.getQuantity());
    //     row.setProductSummaryDTO(dto.getProductSummaryDTO());
    //     row.setStockSU(dto.getStockSU());
    //     row.setPriceSU(dto.getPriceSU());
    //     return row;
    // }

    // public static OrderProductPurchaseRow toPurchaseRow(OrderProductDTO dto){
    //     OrderProductPurchaseRow row = new OrderProductPurchaseRow();
    //     row.setBatch(dto.getBatch());
    //     row.setDiscount(dto.getDiscount());
    //     row.setExpireDate(dto.getExpireDate());
    //     row.setNote(dto.getNote());
    //     row.setUnit(dto.getUnit());
    //     row.setQuantity(dto.getQuantity());
    //     row.setProductSummaryDTO(dto.getProductSummaryDTO());
    //     row.setPrice(dto.getPrice());
    //     return row;
    // }

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
        dto.setQuantitySU(row.getQuantitySU());
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
        dto.setQuantitySU(row.getQuantitySU());
        return dto;
    }

    public static OrderProductSearchRow toRow(OrderProductDTO dto){
        OrderProductSearchRow row = new OrderProductSearchRow();
        row.setBatch(dto.getBatch());
        row.setDiscount(dto.getDiscount());
        row.setExpireDate(dto.getExpireDate());
        row.setNote(dto.getNote());
        row.setProductId(dto.getProductId());
        row.setQuantity(dto.getQuantity());
        row.setUnit(dto.getUnit());
        row.setOrderId(dto.getOrderId());
        row.setProductSummaryDTO(dto.getProductSummaryDTO());
        row.setPriceSU(dto.getPriceSU());
        //row.setStockSU(dto.getStockSU());
        
        return row;
    }

   
}
