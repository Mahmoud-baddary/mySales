package com.baddary.mysales.mapper;

import com.baddary.mysales.dto.OrderDTO;
import com.baddary.mysales.row.OrderSearchRow;

public class OrderMapper {
    private OrderMapper() {
    }

    public static OrderSearchRow toRow(OrderDTO dto) {
        OrderSearchRow row = new OrderSearchRow(dto.getId(),
                dto.getDate(), dto.getCustomerName(), dto.getOrderType(),
                dto.getPaymentType(), dto.getTotalPrice(), dto.getUserName());
        return row;
    }

}
