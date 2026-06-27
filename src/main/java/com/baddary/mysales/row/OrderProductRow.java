package com.baddary.mysales.row;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import java.time.LocalDate;

public class OrderProductRow {
    private final SimpleLongProperty productId = new SimpleLongProperty();
    private final SimpleStringProperty batch = new SimpleStringProperty();
    private final SimpleStringProperty unit = new SimpleStringProperty();
    private final SimpleStringProperty note = new SimpleStringProperty();
    private final SimpleDoubleProperty quantity = new SimpleDoubleProperty();
    private final SimpleDoubleProperty price = new SimpleDoubleProperty();
    private final SimpleDoubleProperty discount = new SimpleDoubleProperty();
    private final SimpleObjectProperty<LocalDate> expireDate = new SimpleObjectProperty<>();
    private final SimpleLongProperty orderId = new SimpleLongProperty();
    private final SimpleStringProperty productName = new SimpleStringProperty();
    private final SimpleDoubleProperty totalPrice = new SimpleDoubleProperty();

    // Default constructor
    public OrderProductRow() {
        totalPrice.bind(price.multiply(quantity).multiply(discount.divide(100).multiply(-1).add(1)));
    }
    public SimpleDoubleProperty totalPriceProperty(){
        return this.totalPrice;
    }

    public String getProductName() {
        return productName.get();
    }

    public void setProductName(String productName) {
        this.productName.set(productName);
    }

    public SimpleStringProperty productNameProperty() {
        return this.productName;
    }

    // Product ID property
    public long getProductId() {
        return productId.get();
    }

    public void setProductId(long productId) {
        this.productId.set(productId);
    }

    public SimpleLongProperty productIdProperty() {
        return productId;
    }

    // Batch
    public String getBatch() {
        return batch.get();
    }

    public void setBatch(String batch) {
        this.batch.set(batch);
    }

    public SimpleStringProperty batchProperty() {
        return batch;
    }

    // Unit
    public String getUnit() {
        return unit.get();
    }

    public void setUnit(String unit) {
        this.unit.set(unit);
    }

    public SimpleStringProperty unitProperty() {
        return unit;
    }

    // Note
    public String getNote() {
        return note.get();
    }

    public void setNote(String note) {
        this.note.set(note);
    }

    public SimpleStringProperty noteProperty() {
        return note;
    }

    // Quantity
    public double getQuantity() {
        return quantity.get();
    }

    public void setQuantity(double quantity) {
        this.quantity.set(quantity);
    }

    public SimpleDoubleProperty quantityProperty() {
        return quantity;
    }

    // Price
    public double getPrice() {
        return price.get();
    }

    public void setPrice(double price) {
        this.price.set(price);
    }

    public SimpleDoubleProperty priceProperty() {
        return price;
    }

    // Discount
    public double getDiscount() {
        return discount.get();
    }

    public void setDiscount(double discount) {
        this.discount.set(discount);
    }

    public SimpleDoubleProperty discountProperty() {
        return discount;
    }

    // Expire Date
    public LocalDate getExpireDate() {
        return expireDate.get();
    }

    public void setExpireDate(LocalDate expireDate) {
        this.expireDate.set(expireDate);
    }

    public SimpleObjectProperty<LocalDate> expireDateProperty() {
        return expireDate;
    }

    // Order ID
    public long getOrderId() {
        return orderId.get();
    }

    public void setOrderId(long orderId) {
        this.orderId.set(orderId);
    }

    public SimpleLongProperty orderIdProperty() {
        return orderId;
    }
}