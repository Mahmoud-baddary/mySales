package com.baddary.mysales.mapper;

import com.baddary.mysales.dto.BarcodeDTO;
import com.baddary.mysales.row.BarcodeRow;

public class BarcodeMapper {
    private BarcodeMapper(){}

    public static BarcodeRow toRow(BarcodeDTO dto){
        BarcodeRow row = new BarcodeRow();
        row.setBarcodeTxt(dto.getBarcodeTxt());
        return row;
    }
    // public static BarcodeDTO toDTO(Barcode entity){
    //     BarcodeDTO dto = new BarcodeDTO();
    //     dto.setBarcodeTxt(entity.getBarcodeTxt());
    //     dto.setProductId(entity.getProduct().getId());
    //     return dto;
    // }
    // public static Barcode toEntity(BarcodeDTO dto){
    //     Barcode entity = new Barcode();
    //     entity.setBarcodeTxt(dto.getBarcodeTxt());
    //     return entity;
    // }
    public static BarcodeDTO toDTO(BarcodeRow row){
        BarcodeDTO dto = new BarcodeDTO();
        dto.setBarcodeTxt(row.getBarcodeTxt());
        return dto;
    }
}
