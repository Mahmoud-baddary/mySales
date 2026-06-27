package com.baddary.mysales.dto;

public class BarcodeDTO {
    private String barcodeTxt;
    private Long productId;

    public BarcodeDTO() {
    }

    public String getBarcodeTxt() {
        return barcodeTxt;
    }

    public void setBarcodeTxt(String barcodeTxt) {
        this.barcodeTxt = barcodeTxt;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }
}
