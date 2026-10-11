package com.baddary.mysales.row;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;

import java.time.LocalDate;

import com.baddary.mysales.dto.ProductSummaryDTO;

public class OrderProductSaleRow {
    private final SimpleLongProperty productId = new SimpleLongProperty(0);
    private final SimpleLongProperty orderId = new SimpleLongProperty(0);

    private final SimpleStringProperty batch = new SimpleStringProperty("");
    private final SimpleStringProperty unit = new SimpleStringProperty("");
    private final SimpleStringProperty note = new SimpleStringProperty("");

    private final SimpleDoubleProperty quantity = new SimpleDoubleProperty(0);
    private final SimpleDoubleProperty price = new SimpleDoubleProperty();
    private final SimpleDoubleProperty discount = new SimpleDoubleProperty(0);
    private final SimpleDoubleProperty stock = new SimpleDoubleProperty();
    private final SimpleDoubleProperty totalPrice = new SimpleDoubleProperty();
    private final ObjectProperty<LocalDate> expireDate = new SimpleObjectProperty<>();
    private final SimpleDoubleProperty stockSU = new SimpleDoubleProperty();
    private final SimpleDoubleProperty priceSU = new SimpleDoubleProperty();
    private final ObjectProperty<ProductSummaryDTO> productSummaryDTO = new SimpleObjectProperty<>();
    private final SimpleDoubleProperty quantitySu = new SimpleDoubleProperty(0);

    public OrderProductSaleRow() {
        totalPrice.bind(price.multiply(quantity).multiply(discount.divide(100).multiply(-1).add(1)));
        stock.bind(Bindings.createDoubleBinding(
                () -> calculateStock(stockSU.get(), productSummaryDTO.get()), stockSU, unit, productSummaryDTO));
        price.bind(
                Bindings.createDoubleBinding(
                        () -> calculatePrice(priceSU.get(), productSummaryDTO.get()),
                        priceSU, unit, productSummaryDTO));
        quantitySu.bind(Bindings.createDoubleBinding(
                () -> calculateQuantitySU(getProductSummaryDTO(), getUnit(), getQuantity()), unit, productSummaryDTO, quantity));

    }

    public SimpleDoubleProperty quantitySUProperty() {
        return this.quantitySu;
    }

    public double getQuantitySU() {
        return this.quantitySu.get();
    }

    public double getTotalPrice() {
        return totalPrice.get();
    }

    public SimpleDoubleProperty totalPriceProperty() {
        return totalPrice;
    }

    public void setPriceSU(double priceSU) {
        this.priceSU.set(priceSU);
    }

    public long getProductId() {
        return productId.get();
    }

    public SimpleLongProperty productIdProperty() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId.set(productId);
    }

    public long getOrderId() {
        return orderId.get();
    }

    public SimpleLongProperty orderIdProperty() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId.set(orderId);
    }

    public String getBatch() {
        return batch.get();
    }

    public SimpleStringProperty batchProperty() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch.set(batch);
    }

    public String getUnit() {
        return unit.get();
    }

    public SimpleStringProperty unitProperty() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit.set(unit);
    }

    public String getNote() {
        return note.get();
    }

    public SimpleStringProperty noteProperty() {
        return note;
    }

    public void setNote(String note) {
        this.note.set(note);
    }

    public double getQuantity() {
        return quantity.get();
    }

    public SimpleDoubleProperty quantityProperty() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity.set(quantity);
    }

    public double getPrice() {
        return price.get();
    }

    public SimpleDoubleProperty priceProperty() {
        return price;
    }

    private double calculatePrice(double priceSU, ProductSummaryDTO productDTO) {
        double price = 0;
        if (productDTO != null) {
            if (this.unit.get().equalsIgnoreCase(productDTO.getGreatestUnit())) {
                price = priceSU * productDTO.getSmallestUnitAmount();
            } else if (this.unit.get().equalsIgnoreCase(productDTO.getMediumUnit())) {
                price = priceSU * productDTO.getSmallestUnitAmount() / productDTO.getMediumUnitAmount();
            } else {
                price = priceSU;
            }
        }
        return price;
    }

    public double getDiscount() {
        return discount.get();
    }

    public SimpleDoubleProperty discountProperty() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount.set(discount);
    }

    public double getStock() {
        return stock.get();
    }

    public SimpleDoubleProperty stockProperty() {
        return stock;
    }

    public LocalDate getExpireDate() {
        return expireDate.get();
    }

    public ObjectProperty<LocalDate> expireDateProperty() {
        return expireDate;
    }

    public void setExpireDate(LocalDate expireDate) {
        this.expireDate.set(expireDate);
    }

    public double getStockSU() {
        return stockSU.get();
    }

    public SimpleDoubleProperty stockSUProperty() {
        return stockSU;
    }

    public double getPriceSU() {
        return priceSU.get();
    }

    public SimpleDoubleProperty priceSUProperty() {
        return priceSU;
    }

    public ProductSummaryDTO getProductSummaryDTO() {
        return productSummaryDTO.get();
    }

    public ObjectProperty<ProductSummaryDTO> productDTOProperty() {
        return productSummaryDTO;
    }

    public void setProductSummaryDTO(ProductSummaryDTO productSummaryDTO) {
        this.productSummaryDTO.set(productSummaryDTO);
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

    private double calculateStock(double stockSU, ProductSummaryDTO productDTO) {
        double stock = 0;
        if (productDTO == null)
            return stock;
        if (this.unit.get().equalsIgnoreCase(productDTO.getGreatestUnit())) {
            stock = stockSU / productDTO.getSmallestUnitAmount();
        } else if (this.unit.get().equalsIgnoreCase(productDTO.getMediumUnit())) {
            stock = stockSU / ((double) productDTO.getSmallestUnitAmount() / productDTO.getMediumUnitAmount());
        } else {
            stock = stockSU;
        }
        return stock;
    }

    @Override
    public String toString() {
        return "OrderProductSaleRow{" +
                "productId=" + productId +
                ", orderId=" + orderId +
                ", batch=" + batch +
                ", unit=" + unit +
                ", note=" + note +
                ", quantity=" + quantity +
                ", price=" + price +
                ", discount=" + discount +
                ", stock=" + stock +
                ", expireDate=" + expireDate +
                '}';
    }

    public void setStockSU(double stockSU) {
        this.stockSU.set(stockSU);
    }
}
