package com.baddary.mysales.row;

import com.baddary.mysales.enums.OrderType;
import javafx.beans.property.*;
import java.time.LocalDate;

public class OrderSearchRow {
    private final LongProperty id = new SimpleLongProperty();
    private final ObjectProperty<LocalDate> date = new SimpleObjectProperty<>();
    private final StringProperty customerName = new SimpleStringProperty();
    private final ObjectProperty<OrderType> orderType = new SimpleObjectProperty<>();
    private final SimpleDoubleProperty paidMoney = new SimpleDoubleProperty();
    private final DoubleProperty finalPrice = new SimpleDoubleProperty();
    private final StringProperty userName = new SimpleStringProperty();

    // Constructor

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

    public double getPaidMoney() { return paidMoney.get(); }
    public SimpleDoubleProperty paidMoneyProperty() { return paidMoney; }
    public void setPaidMoney(double paidMoney) { this.paidMoney.set(paidMoney); }

    public double getFinalPrice() { return finalPrice.get(); }
    public DoubleProperty finalPriceProperty() { return finalPrice; }
    public void setFinalPrice(double price) { this.finalPrice.set(price); }

    public String getUserName() { return userName.get(); }
    public StringProperty userNameProperty() { return userName; }
    public void setUserName(String name) { this.userName.set(name); }
}