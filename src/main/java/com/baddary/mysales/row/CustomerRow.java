package com.baddary.mysales.row;

import javafx.beans.property.SimpleStringProperty;

public class CustomerRow {
    private final SimpleStringProperty name = new SimpleStringProperty();

    public void setName(String name){
        this.name.set(name);
    }
    public String getName(){
        return this.name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }
}
