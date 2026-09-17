package com.baddary.mysales.dto;

import java.time.LocalDate;
import com.baddary.mysales.enums.OrderType;

public class OrderSearchDTO {
    private Long orderId;
    private Long customerId;
    private LocalDate fromDate;
    private LocalDate toDate;
    private OrderType orderType;
    private Long userId;
    private String productBarcode;

    public OrderSearchDTO() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

   public String getProductBarcode() {
    return productBarcode;
}

public void setProductBarcode(String productBarcode) {
    this.productBarcode = productBarcode;
}

 public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

}
