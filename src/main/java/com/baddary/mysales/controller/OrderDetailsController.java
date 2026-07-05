package com.baddary.mysales.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.Optional;

import com.baddary.mysales.dto.OrderDTO;
import com.baddary.mysales.helper.Helper;
import com.baddary.mysales.mapper.OrderProductMapper;
import com.baddary.mysales.row.OrderProductRow;
import com.baddary.mysales.service.OrderApiService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;

public class OrderDetailsController {
    @FXML
    private Label lblOrderId;

    @FXML
    private Label lblDate;

    @FXML
    private Label lblTime;

    @FXML
    private Label lblCustomer;

    @FXML
    private Label lblUser;

    @FXML
    private Label lblOrderType;

    @FXML
    private Label lblPaidMoney;

    @FXML
    private Label lblDiscount;

    @FXML
    private Label lblTotalPrice;

    @FXML
    private TableView<OrderProductRow> tblOrderItems;

    @FXML
    private TableColumn<OrderProductRow, Number> colProductId;

    @FXML
    private TableColumn<OrderProductRow, String> colProductName;

    @FXML
    private TableColumn<OrderProductRow, String> colUnit;

    @FXML
    private TableColumn<OrderProductRow, Number> colQuantity;

    @FXML
    private TableColumn<OrderProductRow, Number> colPrice;

    @FXML
    private TableColumn<OrderProductRow, Number> colDiscount;

    @FXML
    private TableColumn<OrderProductRow, Number> colTotal;

    @FXML
    private TableColumn<OrderProductRow, LocalDate> colExpireDate;

    @FXML
    private TableColumn<OrderProductRow, String> colBatch;

    @FXML
    private TableColumn<OrderProductRow, String> colNotes;

    private final OrderApiService orderApiService = new OrderApiService();
    private final ObservableList<OrderProductRow> orderItemsList = FXCollections.observableArrayList();

    private long orderId;
    private Stage stage;

    public void intialize(Stage stage, long orderId) {
        colProductId.setCellValueFactory(cellData -> cellData.getValue().productIdProperty());
        colUnit.setCellValueFactory(cellData -> cellData.getValue().unitProperty());
        colQuantity.setCellValueFactory(cellData -> cellData.getValue().quantityProperty());
        colPrice.setCellValueFactory(cellData -> cellData.getValue().priceProperty());
        colDiscount.setCellValueFactory(cellData -> cellData.getValue().discountProperty());
        colTotal.setCellValueFactory(cellData -> cellData.getValue().totalPriceProperty()); // need totalProperty in
        colProductName.setCellValueFactory(cellData -> cellData.getValue().productNameProperty()); // OrderProductRow
        colExpireDate.setCellValueFactory(cellData -> cellData.getValue().expireDateProperty());
        colBatch.setCellValueFactory(cellData -> cellData.getValue().batchProperty());
        colNotes.setCellValueFactory(cellData -> cellData.getValue().noteProperty()); // noteProperty in OrderProductRow

        tblOrderItems.setItems(orderItemsList);
        this.orderId = orderId;
        this.stage = stage;
        Task<Optional<OrderDTO>> byIdAsync = orderApiService.findByIdAsync(this.orderId);
        Helper.startTask(byIdAsync, e -> {
            byIdAsync.getValue().ifPresent(dto -> {
                lblCustomer.setText(dto.getCustomerName());
                lblDate.setText(dto.getDate().toString());
                lblDiscount.setText(String.valueOf(dto.getDiscount()));
                lblOrderId.setText(String.valueOf(dto.getId()));
                lblPaidMoney.setText(String.valueOf(dto.getPaidMoney()));
                lblOrderType.setText(dto.getOrderType().name());
                lblTotalPrice.setText(String.valueOf(dto.calculateTotalPrice()));
                lblUser.setText(dto.getUserName());
                lblTime.setText(dto.getTime().toString());
                orderItemsList.clear();
                dto.getOrderProductDTOSet().forEach(op -> {
                    orderItemsList.add(OrderProductMapper.toRow(op));
                });
            });
        }, null, this.stage);
    }

    

    @FXML
    private void handleClose(ActionEvent event) {
    }
}
