package com.baddary.mysales.mapper;

import com.baddary.mysales.dto.ProductDTO;
import com.baddary.mysales.dto.ProductSummaryDTO;
import com.baddary.mysales.row.ProductRow;

public class ProductMapper {
    private ProductMapper() {
    }

    public static ProductRow toRow(ProductDTO dto) {
        ProductRow row = new ProductRow();
        row.setId(dto.getId());
        row.setName(dto.getName());
        return row;
    }

    public static ProductSummaryDTO toSummaryDTO(ProductDTO dto){
        ProductSummaryDTO summaryDTO = new ProductSummaryDTO();
        summaryDTO.setId(dto.getId());
        summaryDTO.setName(dto.getName());
        summaryDTO.setGreatestUnit(dto.getGreatestUnit());
        summaryDTO.setMediumUnit(dto.getMediumUnit());
        summaryDTO.setSmallestUnit(dto.getSmallestUnit());
        summaryDTO.setMediumUnitAmount(dto.getMediumUnitAmount());
        summaryDTO.setSmallestUnitAmount(dto.getSmallestUnitAmount());
        return summaryDTO;
    }

   
}
