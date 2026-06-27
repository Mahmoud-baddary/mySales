package com.baddary.mysales.dto;

import java.util.HashSet;
import java.util.Set;

public class ProductDTO {
    private Long id;
    private String name, greatestUnit, mediumUnit, smallestUnit;
    private int mediumUnitAmount, smallestUnitAmount;
    private final Set<BarcodeDTO> barcodeDTOSet = new HashSet<>();

    public ProductDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGreatestUnit() {
        return greatestUnit;
    }

    public void setGreatestUnit(String greatestUnit) {
        this.greatestUnit = greatestUnit;
    }

    public String getMediumUnit() {
        return mediumUnit;
    }

    public void setMediumUnit(String mediumUnit) {
        this.mediumUnit = mediumUnit;
    }

    public String getSmallestUnit() {
        return smallestUnit;
    }

    public void setSmallestUnit(String smallestUnit) {
        this.smallestUnit = smallestUnit;
    }

    public int getMediumUnitAmount() {
        return mediumUnitAmount;
    }

    public void setMediumUnitAmount(int mediumUnitAmount) {
        this.mediumUnitAmount = mediumUnitAmount;
    }

    public int getSmallestUnitAmount() {
        return smallestUnitAmount;
    }

    public void setSmallestUnitAmount(int smallestUnitAmount) {
        this.smallestUnitAmount = smallestUnitAmount;
    }

    

    public Set<BarcodeDTO> getBarcodeDTOSet() {
        return barcodeDTOSet;
    }
    

}
