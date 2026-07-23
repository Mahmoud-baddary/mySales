package com.baddary.mysales.row;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

public class CustomerBalanceRow {
    private final SimpleLongProperty id = new SimpleLongProperty(0);
    private final SimpleStringProperty name = new SimpleStringProperty();
    private final SimpleDoubleProperty balance = new SimpleDoubleProperty();
    private final SimpleStringProperty status = new SimpleStringProperty();
    public SimpleLongProperty idProperty() {
        return id;
    }
    public SimpleStringProperty nameProperty() {
        return name;
    }
    public SimpleDoubleProperty balanceProperty() {
        return balance;
    }
    public SimpleStringProperty statusProperty() {
        return status;
    }
    public long getId(){
        return id.get();
    }
    public String getName(){
        return name.get();
    }
    public double getBalance(){
        return balance.get();
    }
    public String getStatus(){
        return status.get();
    }
    public void setId(Long id){
        this.id.set(id);
    }
    public void setName(String name){
        this.name.set(name);
    }
    public void setBalance(double balance){
        this.balance.set(Math.abs(balance));
    }
    public void setStatus(String status){
        this.status.set(status);
    }
    
}
