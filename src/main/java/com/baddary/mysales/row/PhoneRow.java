package com.baddary.mysales.row;

import javafx.beans.property.SimpleStringProperty;

public class PhoneRow {
    private final SimpleStringProperty number = new SimpleStringProperty();
    public void setNumber(String number){
        this.number.set(number);
    }
    public String getNumber (){
        return this.number.get();
    }

    public SimpleStringProperty numberProperty() {
        return number;
    }
}
