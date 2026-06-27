package com.baddary.mysales.dto;

import java.time.LocalDate;

public class StockDTO {
    private Long id;
    private Long productId;
    private LocalDate expire;
    private double quantitySU;
    private String batch;
    private double priceSU;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public LocalDate getExpire() {
        return expire;
    }

    public void setExpire(LocalDate expire) {
        this.expire = expire;
    }

    public double getQuantitySU() {
        return quantitySU;
    }

    public void setQuantitySU(double quantitySU) {
        this.quantitySU = quantitySU;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public double getPriceSU() {
        return priceSU;
    }

    public void setPriceSU(double priceSU) {
        this.priceSU = priceSU;
    }
}
