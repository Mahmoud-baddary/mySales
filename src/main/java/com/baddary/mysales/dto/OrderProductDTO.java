package com.baddary.mysales.dto;

import java.time.LocalDate;

public class OrderProductDTO {
    private Long productId;
    private String batch, unit, note;
    private double quantity, price, discount, quantitySU;
    private LocalDate expireDate;
    private Long orderId;
    private ProductSummaryDTO productSummaryDTO;
    private double stockSU;
    private double priceSU;

    public Long getOrderId(){
        return orderId;
    }

    

    public void setOrderId(Long orderId){
        this.orderId = orderId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public LocalDate getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(LocalDate expireDate) {
        this.expireDate = expireDate;
    }

    public ProductSummaryDTO getProductSummaryDTO() {
        return productSummaryDTO;
    }

    public void setProductSummaryDTO(ProductSummaryDTO productSummaryDTO) {
        this.productSummaryDTO = productSummaryDTO;
    }



    public double getQuantitySU() {
        return quantitySU;
    }



    public void setQuantitySU(double quantitySU) {
        this.quantitySU = quantitySU;
    }



    public double getStockSU() {
        return stockSU;
    }



    public void setStockSU(double stockSU) {
        this.stockSU = stockSU;
    }



    public double getPriceSU() {
        return priceSU;
    }



    public void setPriceSU(double priceSU) {
        this.priceSU = priceSU;
    }
}
