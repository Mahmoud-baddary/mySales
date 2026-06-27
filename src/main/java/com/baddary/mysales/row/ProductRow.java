package com.baddary.mysales.row;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

public class ProductRow {
    private final SimpleLongProperty id = new SimpleLongProperty();
    private final SimpleStringProperty name = new SimpleStringProperty();
    public ProductRow(){

    }
    public void setId(Long id){
        this.id.set(id);
    }
    public Long getId(){
        return this.id.get();
    }
    public SimpleLongProperty idProperty(){
        return this.id;
    }
    public void setName(String name){
        this.name.set(name);
    }
    public SimpleStringProperty nameProperty(){
        return this.name;
    }
    public String getName (){
        return this.name.get();
    }

    @Override
    public String toString() {
        return this.name.get();
    }
}
