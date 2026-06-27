package com.baddary.mysales.row;

import com.baddary.mysales.dto.ProductDTO;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;

public class StockRow {
    private final SimpleStringProperty productName = new SimpleStringProperty();
    private final SimpleStringProperty unit = new SimpleStringProperty();
    private final SimpleDoubleProperty quantity = new SimpleDoubleProperty();
    private final SimpleDoubleProperty quantitySU = new SimpleDoubleProperty();
    private final SimpleStringProperty expire = new SimpleStringProperty();
    private final SimpleDoubleProperty priceSU = new SimpleDoubleProperty();
    private final SimpleDoubleProperty price = new SimpleDoubleProperty();
    private final ObjectProperty<ProductDTO> productDTO = new SimpleObjectProperty<>();

    public StockRow() {
        quantity.bind(Bindings.createDoubleBinding(
                () -> calculateQuantity(quantitySU.get(), productDTO.get()), quantitySU, unit, productDTO
        ));
        price.bind(
                Bindings.createDoubleBinding(
                        () -> calculatePrice(priceSU.get(), productDTO.get()),
                        priceSU, unit, productDTO
                )
        );
    }

    public String getProductName() {
        return productName.get();
    }

    public SimpleStringProperty productNameProperty() {
        return productName;
    }

    public String getUnit() {
        return unit.get();
    }

    public SimpleStringProperty unitProperty() {
        return unit;
    }

    public double getQuantity() {
        return quantity.get();
    }

    public SimpleDoubleProperty quantityProperty() {
        return quantity;
    }

    public double getQuantitySU() {
        return quantitySU.get();
    }

    public SimpleDoubleProperty quantitySUProperty() {
        return quantitySU;
    }

    public String getExpire() {
        return expire.get();
    }

    public SimpleStringProperty expireProperty() {
        return expire;
    }

    public double getPriceSU() {
        return priceSU.get();
    }

    public SimpleDoubleProperty priceSUProperty() {
        return priceSU;
    }

    public double getPrice() {
        return price.get();
    }

    public SimpleDoubleProperty priceProperty() {
        return price;
    }

    public ProductDTO getProductDTO() {
        return productDTO.get();
    }

    public ObjectProperty<ProductDTO> productDTOProperty() {
        return productDTO;
    }

    public void setUnit(String unit){
        this.unit.set(unit);
    }

    public void setQuantitySU(double quantitySU){
        this.quantitySU.set(quantitySU);
    }

    public void setPriceSU(double priceSU){
        this.priceSU.set(priceSU);
    }

    public void setProductName(String productName){
        this.productName.set(productName);
    }

    public void setExpire(String expire){
        this.expire.set(expire);
    }

    public void setProductDTO(ProductDTO dto){
        this.productDTO.set(dto);
    }


    private double calculateQuantity(double quantitySU, ProductDTO productDTO) {
        double quantity = 0;
        if (productDTO == null) return quantity;
        if (this.unit.get().equalsIgnoreCase(productDTO.getGreatestUnit())) {
            quantity = quantitySU / productDTO.getSmallestUnitAmount();
        } else if (this.unit.get().equalsIgnoreCase(productDTO.getMediumUnit())) {
            quantity = quantitySU / ((double) productDTO.getSmallestUnitAmount() / productDTO.getMediumUnitAmount());
        } else {
            quantity = quantitySU;
        }
        return quantity;
    }

    private double calculatePrice(double priceSU, ProductDTO productDTO) {
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
}
