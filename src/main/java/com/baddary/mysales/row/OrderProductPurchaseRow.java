package com.baddary.mysales.row;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;

import java.time.LocalDate;

import com.baddary.mysales.dto.ProductSummaryDTO;

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
    private final ObjectProperty<ProductSummaryDTO> productSummaryDTO = new SimpleObjectProperty<>();
    private final SimpleDoubleProperty quantitySu = new SimpleDoubleProperty(0);
    private final SimpleDoubleProperty priceSU = new SimpleDoubleProperty();

    public OrderProductPurchaseRow() {
        totalPrice.bind(price.multiply(quantity).multiply(discount.divide(100).multiply(-1).add(1)));
        quantitySu.bind(Bindings.createDoubleBinding(
                () -> calculateQuantitySU(getProductSummaryDTO(), getUnit(), getQuantity()), unit, productSummaryDTO, quantity));
            priceSU.bind(Bindings.createDoubleBinding(
                () -> calculatePriceSU(getProductSummaryDTO(), getUnit(), getPrice()), unit, productSummaryDTO, price));
            }

    public double getPriceSU() {
        return priceSU.get();
    }
    public SimpleDoubleProperty priceSUProperty() {
        return priceSU;
    }

    public SimpleDoubleProperty quantitySUProperty() {
        return this.quantitySu;
    }

    public double getQuantitySU() {
        return this.quantitySu.get();
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

    public ProductSummaryDTO getProductSummaryDTO() {
        return productSummaryDTO.get();
    }

    public ObjectProperty<ProductSummaryDTO> productDTOProperty() {
        return productSummaryDTO;
    }

    private double calculateQuantitySU(ProductSummaryDTO dto, String unit, double quantity) {
        double quantitySU = 0;
        if (dto == null) {
            return quantitySU;
        }
        if (unit.equalsIgnoreCase(dto.getGreatestUnit())) {
            quantitySU = quantity * dto.getSmallestUnitAmount();
        } else if (unit.equalsIgnoreCase(dto.getMediumUnit())) {
            quantitySU = quantity * dto.getSmallestUnitAmount() / dto.getMediumUnitAmount();
        } else {
            quantitySU = quantity;
        }
        return quantitySU;
    }

    public void setProductId(long productId) {
        this.productId.set(productId);
    }

    public void setOrderId(long orderId) {
        this.orderId.set(orderId);
    }

    public void setBatch(String batch) {
        this.batch.set(batch);
    }

    public void setUnit(String unit) {
        this.unit.set(unit);
    }

    public void setNote(String note) {
        this.note.set(note);
    }

    public void setQuantity(double quantity) {
        this.quantity.set(quantity);
    }

    public void setPrice(double price) {
        this.price.set(price);
    }

    public void setDiscount(double discount) {
        this.discount.set(discount);
    }

    public void setExpireDate(LocalDate expireDate) {
        this.expireDate.set(expireDate);
    }

    public void setProductSummaryDTO(ProductSummaryDTO productSummaryDTO) {
        this.productSummaryDTO.set(productSummaryDTO);
    }

    private double calculatePriceSU(ProductSummaryDTO dto, String unit, double price) {
        double priceSU = 0;
        if (dto == null) {
            return priceSU;
        }
        if (unit.equalsIgnoreCase(dto.getGreatestUnit())) {
            priceSU = price / dto.getSmallestUnitAmount();
        } else if (unit.equalsIgnoreCase(dto.getMediumUnit())) {
            priceSU = price / ((double) dto.getSmallestUnitAmount() / dto.getMediumUnitAmount());
        } else {
            priceSU = price;
        }
        return priceSU;
    }
}
