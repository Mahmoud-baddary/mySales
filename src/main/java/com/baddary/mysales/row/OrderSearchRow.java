package com.baddary.mysales.row;

import com.baddary.mysales.enums.OrderType;
import com.baddary.mysales.enums.PaymentType;
import javafx.beans.property.*;
import java.time.LocalDate;

public class OrderSearchRow {
    private final LongProperty id = new SimpleLongProperty();
    private final ObjectProperty<LocalDate> date = new SimpleObjectProperty<>();
    private final StringProperty customerName = new SimpleStringProperty();
    private final ObjectProperty<OrderType> orderType = new SimpleObjectProperty<>();
    private final ObjectProperty<PaymentType> paymentType = new SimpleObjectProperty<>();
    private final DoubleProperty finalPrice = new SimpleDoubleProperty();
    private final StringProperty userName = new SimpleStringProperty();

    // Constructor
    public OrderSearchRow(long id, LocalDate date, String customerName,
                          OrderType orderType, PaymentType paymentType,
                          double totalPrice, String userName) {
        setId(id);
        setDate(date);
        setCustomerName(customerName);
        setOrderType(orderType);
        setPaymentType(paymentType);
        setFinalPrice(totalPrice);
        setUserName(userName);
    }

    // Getters and property getters (required for PropertyValueFactory)
    public long getId() { return id.get(); }
    public LongProperty idProperty() { return id; }
    public void setId(long id) { this.id.set(id); }

    public LocalDate getDate() { return date.get(); }
    public ObjectProperty<LocalDate> dateProperty() { return date; }
    public void setDate(LocalDate date) { this.date.set(date); }

    public String getCustomerName() { return customerName.get(); }
    public StringProperty customerNameProperty() { return customerName; }
    public void setCustomerName(String name) { this.customerName.set(name); }

    public OrderType getOrderType() { return orderType.get(); }
    public ObjectProperty<OrderType> orderTypeProperty() { return orderType; }
    public void setOrderType(OrderType type) { this.orderType.set(type); }

    public PaymentType getPaymentType() { return paymentType.get(); }
    public ObjectProperty<PaymentType> paymentTypeProperty() { return paymentType; }
    public void setPaymentType(PaymentType type) { this.paymentType.set(type); }

    public double getFinalPrice() { return finalPrice.get(); }
    public DoubleProperty finalPriceProperty() { return finalPrice; }
    public void setFinalPrice(double price) { this.finalPrice.set(price); }

    public String getUserName() { return userName.get(); }
    public StringProperty userNameProperty() { return userName; }
    public void setUserName(String name) { this.userName.set(name); }
}