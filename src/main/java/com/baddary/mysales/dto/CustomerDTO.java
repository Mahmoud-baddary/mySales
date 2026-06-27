package com.baddary.mysales.dto;


import java.util.HashSet;
import java.util.Set;

public class CustomerDTO {
    private Long id;
    private String name, email, address;
    private final Set<PhoneDTO> phoneDTOSet = new HashSet<>();

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Set<PhoneDTO> getPhoneDTOSet() {
        return phoneDTOSet;
    }
}
