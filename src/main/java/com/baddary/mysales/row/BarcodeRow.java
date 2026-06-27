package com.baddary.mysales.row;

import javafx.beans.property.SimpleStringProperty;

public class BarcodeRow {
    private final SimpleStringProperty barcodeTxt = new SimpleStringProperty();
    public BarcodeRow(){}
    public void setBarcodeTxt(String barcodeTxt){
        this.barcodeTxt.set(barcodeTxt);
    }
    public SimpleStringProperty barcodeTxtProperty(){
        return barcodeTxt;
    }
    public String getBarcodeTxt(){
        return this.barcodeTxt.get();
    }
}
