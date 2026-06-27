package com.baddary.mysales.mapper;

import com.baddary.mysales.dto.PhoneDTO;
import com.baddary.mysales.row.PhoneRow;

public class PhoneMapper {
    private PhoneMapper(){

    }

    // public static Phone toEntity(PhoneDTO dto){
    //     Phone phone = new Phone();
    //     phone.setPhoneNum(dto.getPhoneNum());
    //     return phone;
    // }
    // public static PhoneDTO toDTO(Phone entity){
    //     PhoneDTO dto = new PhoneDTO();
    //     dto.setId(entity.getId());
    //     dto.setPhoneNum(entity.getPhoneNum());
    //     dto.setCustomerId(entity.getCustomer().getId());
    //     return dto;
    // }
    public static PhoneRow toRow(PhoneDTO dto){
        PhoneRow row = new PhoneRow();
        row.setNumber(dto.getPhoneNum());
        return row;
    }
    public static PhoneDTO toDTO(PhoneRow row){
        PhoneDTO dto = new PhoneDTO();
        dto.setPhoneNum(row.getNumber());
        return dto;
    }
}
