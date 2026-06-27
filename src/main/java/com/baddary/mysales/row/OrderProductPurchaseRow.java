package com.baddary.mysales.row;

import javafx.beans.property.*;

import java.time.LocalDate;

import com.baddary.mysales.dto.ProductDTO;

public class OrderProductPurchaseRow {
    private final SimpleLongProperty productId = new SimpleLongProperty(0);
    private final SimpleLongProperty orderId = new SimpleLongProperty(0);

    private final SimpleStringProperty batch = new SimpleStringProperty("");
    private final SimpleStringProperty unit = new SimpleStringProperty("");
    private final SimpleStringProperty note = new SimpleStringProperty("");

    private final SimpleDoubleProperty quantity = new SimpleDoubleProperty(0);
    private final SimpleDoubleProperty price = new SimpleDoubleProperty(0);
    private final SimpleDoubleProperty discount = new SimpleDoubleProperty(0);
    private final SimpleDoubleProperty totalPrice = new SimpleDoubleProperty();
    private final ObjectProperty<LocalDate> expireDate = new SimpleObjectProperty<>();
    private final ObjectProperty<ProductDTO> productDTO = new SimpleObjectProperty<>();
    public OrderProductPurchaseRow(){
        totalPrice.bind(price.multiply(quantity).multiply(discount.divide(100).multiply(-1).add(1)));
    }

    public long getProductId() {
        return productId.get();
    }

    public SimpleLongProperty productIdProperty() {
        return productId;
    }

    public long getOrderId() {
        return orderId.get();
    }

    public SimpleLongProperty orderIdProperty() {
        return orderId;
    }


    public String getBatch() {
        return batch.get();
    }

    public SimpleStringProperty batchProperty() {
        return batch;
    }

    public String getUnit() {
        return unit.get();
    }

    public SimpleStringProperty unitProperty() {
        return unit;
    }

    public String getNote() {
        return note.get();
    }

    public SimpleStringProperty noteProperty() {
        return note;
    }

    public double getQuantity() {
        return quantity.get();
    }

    public SimpleDoubleProperty quantityProperty() {
        return quantity;
    }

    public double getPrice() {
        return price.get();
    }

    public SimpleDoubleProperty priceProperty() {
        return price;
    }

    public double getDiscount() {
        return discount.get();
    }

    public SimpleDoubleProperty discountProperty() {
        return discount;
    }

    public double getTotalPrice() {
        return totalPrice.get();
    }

    public SimpleDoubleProperty totalPriceProperty() {
        return totalPrice;
    }

    public LocalDate getExpireDate() {
        return expireDate.get();
    }

    public ObjectProperty<LocalDate> expireDateProperty() {
        return expireDate;
    }

    public ProductDTO getProductDTO() {
        return productDTO.get();
    }

    public ObjectProperty<ProductDTO> productDTOProperty() {
        return productDTO;
    }

    public void setProductId(long productId) { this.productId.set(productId); }
    public void setOrderId(long orderId) { this.orderId.set(orderId); }
    public void setBatch(String batch) { this.batch.set(batch); }
    public void setUnit(String unit) { this.unit.set(unit); }
    public void setNote(String note) { this.note.set(note); }
    public void setQuantity(double quantity) { this.quantity.set(quantity); }
    public void setPrice(double price) { this.price.set(price); }
    public void setDiscount(double discount) { this.discount.set(discount); }
    public void setExpireDate(LocalDate expireDate) { this.expireDate.set(expireDate); }
    public void setProductDTO(ProductDTO productDTO) { this.productDTO.set(productDTO); }
}
