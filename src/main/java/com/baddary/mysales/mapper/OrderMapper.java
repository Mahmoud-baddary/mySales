package com.baddary.mysales.mapper;

import com.baddary.mysales.dto.OrderDTO;
import com.baddary.mysales.row.OrderSearchRow;

public class OrderMapper {
    private OrderMapper() {
    }

    public static OrderSearchRow toRow(OrderDTO dto) {
        OrderSearchRow row = new OrderSearchRow();
        row.setCustomerName(dto.getCustomerName());
        row.setDate(dto.getDate());
        row.setFinalPrice(dto.calculateTotalPrice());
        row.setId(dto.getId());
        row.setOrderType(dto.getOrderType());
        row.setPaidMoney(dto.getPaidMoney());
        row.setUserName(dto.getUserName());
        return row;
    }

}
