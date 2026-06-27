package com.baddary.mysales.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

import com.baddary.mysales.enums.OrderType;
import com.baddary.mysales.enums.PaymentType;

public class OrderDTO {
    private Long id;
    private LocalDate date;
    private LocalTime time;
    private PaymentType paymentType;
    private OrderType orderType;
    private double discount;
    private Long customerId;
    private Long userId;
    private final Set<OrderProductDTO> orderProductDTOSet = new HashSet<>();
    private String userName;
    private String customerName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Set<OrderProductDTO> getOrderProductDTOSet() {
        return orderProductDTOSet;
    }

    public double getTotalPrice() {
        return orderProductDTOSet.stream()
                .mapToDouble(item -> (item.getQuantity() * item.getPrice()) * (1 - item.getDiscount() / 100.0))
                .sum() * (1 - discount / 100.0);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
}
