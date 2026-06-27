package com.baddary.mysales.mapper;

import com.baddary.mysales.dto.ProductDTO;
import com.baddary.mysales.row.ProductRow;

public class ProductMapper {
    private ProductMapper() {
    }

    // public static ProductDTO toDTO(Product entity) {
    //     ProductDTO dto = new ProductDTO();
    //     dto.setName(entity.getName());
    //     dto.setId(entity.getId());
    //     dto.setsUAmount(entity.getsUAmount());
    //     dto.setmUAmount(entity.getmUAmount());
    //     dto.setSmallestUnit(entity.getSmallestUnit());
    //     dto.setMediumUnit(entity.getMediumUnit());
    //     dto.setGreatestUnit(entity.getGreatestUnit());
    //     entity.getBarcodes().stream().map(BarcodeMapper::toDTO).forEach(dto.getBarcodeDTOSet()::add);
    //     return dto;
    // }

    // public static Product toEntity(ProductDTO dto) {
    //     Product entity = new Product();
    //     entity.setName(dto.getName());
    //     entity.setsUAmount(dto.getsUAmount());
    //     entity.setmUAmount(dto.getmUAmount());
    //     entity.setGreatestUnit(dto.getGreatestUnit());
    //     entity.setMediumUnit(dto.getMediumUnit());
    //     entity.setSmallestUnit(dto.getSmallestUnit());
    //     dto.getBarcodeDTOSet().forEach(barcodeDTO->{
    //         Barcode barcode = BarcodeMapper.toEntity(barcodeDTO);
    //         entity.addBarcode(barcode);
    //     });
    //     return entity;
    // }

    public static ProductRow toRow(ProductDTO dto) {
        ProductRow row = new ProductRow();
        row.setId(dto.getId());
        row.setName(dto.getName());
        return row;
    }

    // public static void updateEntity(Product entity, ProductDTO dto) {
    //     entity.setName(dto.getName());
    //     entity.setsUAmount(dto.getsUAmount());
    //     entity.setmUAmount(dto.getmUAmount());
    //     entity.setMediumUnit(dto.getMediumUnit());
    //     entity.setSmallestUnit(dto.getSmallestUnit());
    //     dto.getBarcodeDTOSet().forEach(barcodeDTO->{
    //         Barcode barcode = BarcodeMapper.toEntity(barcodeDTO);
    //         entity.addBarcode(barcode);
    //     });
    // }
}
