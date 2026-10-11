package com.baddary.mysales.dto;

public class ProductSummaryDTO {
    private Long id;
    private String name;
    private String greatestUnit;
    private String mediumUnit;
    private String smallestUnit;
    private int mediumUnitAmount;
    private int smallestUnitAmount;
    public ProductSummaryDTO() {
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
    
}
