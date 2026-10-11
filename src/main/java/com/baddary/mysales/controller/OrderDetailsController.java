package com.baddary.mysales.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;

import com.baddary.mysales.MainApplication;
import com.baddary.mysales.dto.OrderDTO;
import com.baddary.mysales.enums.OrderType;
import com.baddary.mysales.helper.Helper;
import com.baddary.mysales.mapper.OrderProductMapper;
import com.baddary.mysales.row.OrderProductSearchRow;
import com.baddary.mysales.service.OrderApiService;

import javafx.beans.property.SimpleStringProperty;
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
    private TableView<OrderProductSearchRow> tblOrderItems;

    @FXML
    private TableColumn<OrderProductSearchRow, Number> colProductId;

    @FXML
    private TableColumn<OrderProductSearchRow, String> colProductName;

    @FXML
    private TableColumn<OrderProductSearchRow, String> colUnit;

    @FXML
    private TableColumn<OrderProductSearchRow, Number> colQuantity;

    @FXML
    private TableColumn<OrderProductSearchRow, Number> colPrice;

    @FXML
    private TableColumn<OrderProductSearchRow, Number> colDiscount;

    @FXML
    private TableColumn<OrderProductSearchRow, Number> colTotal;

    @FXML
    private TableColumn<OrderProductSearchRow, LocalDate> colExpireDate;

    @FXML
    private TableColumn<OrderProductSearchRow, String> colBatch;

    @FXML
    private TableColumn<OrderProductSearchRow, String> colNotes;

    private final OrderApiService orderApiService = new OrderApiService();
    private final ObservableList<OrderProductSearchRow> orderItemsList = FXCollections.observableArrayList();

    private Long orderId;
    private Stage stage;
    private OrderType orderType;

    public void intialize(Stage stage, long orderId) {
        colProductId.setCellValueFactory(cellData -> cellData.getValue().productIdProperty());
        colUnit.setCellValueFactory(cellData -> cellData.getValue().unitProperty());
        colQuantity.setCellValueFactory(cellData -> cellData.getValue().quantityProperty());
        colPrice.setCellValueFactory(cellData -> cellData.getValue().priceProperty());
        colDiscount.setCellValueFactory(cellData -> cellData.getValue().discountProperty());
        colTotal.setCellValueFactory(cellData -> cellData.getValue().totalPriceProperty()); // need totalProperty in
        colProductName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProductSummaryDTO().getName())); // OrderProductRow
        colExpireDate.setCellValueFactory(cellData -> cellData.getValue().expireDateProperty());
        colBatch.setCellValueFactory(cellData -> cellData.getValue().batchProperty());
        colNotes.setCellValueFactory(cellData -> cellData.getValue().noteProperty()); // noteProperty in OrderProductRow

        tblOrderItems.setItems(orderItemsList);
        this.orderId = orderId;
        this.stage = stage;
        Task<OrderDTO> byIdAsync = orderApiService.findByIdAsync(this.orderId);
        Helper.startTask(byIdAsync, e -> {
            OrderDTO dto = byIdAsync.getValue();

            lblCustomer.setText(dto.getCustomerName());
            lblDate.setText(dto.getDate().toString());
            lblDiscount.setText(String.valueOf(dto.getDiscount()));
            lblOrderId.setText(String.valueOf(dto.getId()));
            lblPaidMoney.setText(String.valueOf(dto.getPaidMoney()));
            lblOrderType.setText(dto.getOrderType().name());
            this.orderType = dto.getOrderType();
            lblTotalPrice.setText(String.valueOf(dto.calculateTotalPrice()));
            lblUser.setText(dto.getUserName());
            lblTime.setText(dto.getTime().toString());
            orderItemsList.clear();
            dto.getOrderProductDTOSet().forEach(op -> {
                orderItemsList.add(OrderProductMapper.toRow(op));

            });
        }, null, this.stage);
    }

    @FXML
    private void handleClose(ActionEvent event) {
        this.stage.close();
    }

    @FXML
    private void handleReturn(ActionEvent event) throws IOException {
        if (this.orderType == OrderType.SALE) {
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("sales_order_view.fxml"));
            Parent root = loader.load();
            SalesOrderController controller = loader.getController();
            controller.initialize(this.stage);
            this.stage.setTitle("Return Buy Order");
            this.stage.setScene(new Scene(root));
            this.stage.sizeToScene();
        }else{
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("purchase_order_view.fxml"));
            Parent root = loader.load();
            PurchaseOrderController controller = loader.getController();
            controller.initialize(this.stage);
            this.stage.setTitle("Return Buy Order");
            this.stage.setScene(new Scene(root));
            this.stage.sizeToScene();
        }
    }

}
