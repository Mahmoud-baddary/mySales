package com.baddary.mysales.mapper;

import com.baddary.mysales.dto.CustomerDTO;
import com.baddary.mysales.enums.CustomerStatus;
import com.baddary.mysales.row.CustomerBalanceRow;
import com.baddary.mysales.row.CustomerRow;

public class CustomerMapper {
    private CustomerMapper() {
    }

    // public static CustomerDTO toDTO(Customer entity) {
    // CustomerDTO dto = new CustomerDTO();
    // dto.setAddress(entity.getAddress());
    // dto.setEmail(entity.getEmail());
    // dto.setId(entity.getId());
    // dto.setName(entity.getName());
    // dto.setId(entity.getId());
    // dto.getPhoneDTOSet().addAll(entity.getPhones().stream().map(PhoneMapper::toDTO).toList());
    // return dto;
    // }

    public static CustomerRow toRow(CustomerDTO dto) {
        CustomerRow row = new CustomerRow();
        row.setName(dto.getName());
        return row;
    }

    public static CustomerBalanceRow toBalanceRow(CustomerDTO dto) {
        CustomerBalanceRow row = new CustomerBalanceRow();
        row.setId(dto.getId());
        row.setName(dto.getName());
        double balance = dto.getBalance();
        row.setBalance(balance);
        if (balance == 0) {
            row.setStatus(CustomerStatus.SETTLED.name());
        } else if (balance > 0) {
            row.setStatus(CustomerStatus.OWES.name());
        } else {
            row.setStatus(CustomerStatus.DESERVES.name());
        }
        return row;

    }

    // public static Customer toEntity(CustomerDTO dto) {
    // Customer entity = new Customer();
    // entity.setAddress(dto.getAddress());
    // entity.setEmail(dto.getEmail());
    // entity.setName(dto.getName());
    // entity.setId(dto.getId());
    // dto.getPhoneDTOSet().forEach(phoneDTO->{
    // Phone phone = PhoneMapper.toEntity(phoneDTO);
    // entity.addPhone(phone);
    // });
    // return entity;
    // }

    // public static void updateEntity(Customer entity, CustomerDTO dto) {
    // entity.setAddress(dto.getAddress());
    // entity.setEmail(dto.getEmail());
    // entity.setName(dto.getName());
    // dto.getPhoneDTOSet().forEach(phoneDTO->{
    // Phone phone = PhoneMapper.toEntity(phoneDTO);
    // entity.addPhone(phone);
    // });
    // }

}
