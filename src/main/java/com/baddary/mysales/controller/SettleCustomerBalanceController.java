package com.baddary.mysales.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ComboBox;

import java.time.LocalDate;
import java.util.List;

import com.baddary.mysales.dto.OrderDTO;
import com.baddary.mysales.enums.CustomerStatus;
import com.baddary.mysales.enums.OrderType;
import com.baddary.mysales.helper.Helper;
import com.baddary.mysales.mapper.OrderMapper;
import com.baddary.mysales.row.CustomerBalanceRow;
import com.baddary.mysales.row.OrderSearchRow;
import com.baddary.mysales.service.OrderApiService;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;

public class SettleCustomerBalanceController {

    @FXML
    private TextField tfCustomerName;

    @FXML
    private DatePicker dpFromDate;
    @FXML
    private DatePicker dpToDate;
    @FXML
    private ComboBox<String> cbOrderType;
    @FXML
    private Button btnSearch;
    @FXML
    private Button btnReset;
    @FXML
    private TableView<OrderSearchRow> tblOrders;
    @FXML
    private TableColumn<OrderSearchRow, Number> colOrderId;
    @FXML
    private TableColumn<OrderSearchRow, LocalDate> colOrderDate;
    @FXML
    private TableColumn<OrderSearchRow, OrderType> colOrderType;
    @FXML
    private TableColumn<OrderSearchRow, Number> colPaidMoney;
    @FXML
    private TableColumn<OrderSearchRow, Number> colFinalPrice;
    @FXML
    private TableColumn<OrderSearchRow, String> colUser;
    @FXML
    private Button btnViewDetails;
    @FXML
    private Button btnClose;
    @FXML
    private TextField tfBalance;
    @FXML
    private TextField tfOrdersPrice;
    @FXML
    private Button btnSettle;

    private Stage stage;
    private CustomerBalanceRow customer;

    private OrderApiService orderApiService = new OrderApiService();
    private final ObservableList<OrderSearchRow> orderRows = FXCollections.observableArrayList();
    @FXML
    private TextField tfBalanceDiscount;
    @FXML
    private TextField tfAmountToPay;
    @FXML
    private Button btnMakeBalance;
    @FXML
    private TextField tfStatus;

    public void initialize(Stage stage, CustomerBalanceRow customer) {
        this.stage = stage;
        this.customer = customer;
        tfCustomerName.setText(customer.getName());
        tfBalance.setText(String.valueOf(customer.getBalance()));
        cbOrderType.getItems().addAll(OrderType.BUY.name(), OrderType.SALE.name());
        // select order type according to status
        if (customer.getStatus().equals(CustomerStatus.DESERVES.name())) {
            cbOrderType.setValue(OrderType.BUY.name());
        }else{
            cbOrderType.setValue(OrderType.SALE.name());
        }
        tfStatus.setText(customer.getStatus());
        // table value
        tblOrders.setItems(orderRows);
        colFinalPrice.setCellValueFactory(cellData-> cellData.getValue().finalPriceProperty());
        colOrderDate.setCellValueFactory(cellData-> cellData.getValue().dateProperty());
        colOrderId.setCellValueFactory(cellData-> cellData.getValue().idProperty());
        colOrderType.setCellValueFactory(cellData-> cellData.getValue().orderTypeProperty());
        colPaidMoney.setCellValueFactory(cellData-> cellData.getValue().paidMoneyProperty());
        colUser.setCellValueFactory(cellData-> cellData.getValue().userNameProperty());

        // bind total remaining
        DoubleBinding totalRemainingBinding = Bindings.createDoubleBinding(
                () -> orderRows.stream().mapToDouble(OrderSearchRow::getRemainingMoney).sum(), orderRows);

        tfOrdersPrice.textProperty().bind(totalRemainingBinding.asString("%.2f"));

        // Bind tfAmount to the calculation of balance and discount
        tfAmountToPay.textProperty().bind(Bindings.createStringBinding(() -> {
            double balance = parseDouble(tfBalance.getText());
            double discount = parseDouble(tfBalanceDiscount.getText());
            double amount = balance *(1 - discount/100);
            return String.format("%.2f", amount);
        }, tfBalance.textProperty(), tfBalanceDiscount.textProperty()));
    }

    private void loadOrders(long customerId) {
        LocalDate fromDate = dpFromDate.getValue();
        if (fromDate == null) {
            Helper.createAlertError("Missing value", "From Date can't be empty").show();
            return;
        }
        LocalDate toDate = dpToDate.getValue();
        if (toDate == null) {
            Helper.createAlertError("Missing value", "To Date can't be empty").show();
            return;
        }

        OrderType orderType = null;
        String orderTypeStr = cbOrderType.getValue();
        if (orderTypeStr != null && !orderTypeStr.equals("ALL")) {
            orderType = OrderType.valueOf(orderTypeStr);
        }

        Task<List<OrderDTO>> searchOrdersAsync = orderApiService.searchOrdersAsync(customerId, fromDate, toDate,
                orderType);
        ;
        Helper.startTask(searchOrdersAsync, e -> {
            List<OrderDTO> orders = searchOrdersAsync.getValue();
            orderRows.clear();
            orderRows.addAll(orders.stream().map(OrderMapper::toRow).toList());
        }, null, this.stage);

    }

    @FXML
    private void handleSearch(ActionEvent event) {
        loadOrders(this.customer.getId());
    }

    @FXML
    private void handleReset(ActionEvent event) {

        dpFromDate.setValue(null);
        dpToDate.setValue(null);
        orderRows.clear();
        tfBalance.setText(String.valueOf(this.customer.getBalance()));

    }

    @FXML
    private void handleViewDetails(ActionEvent event) {
    }

    @FXML
    private void handleClose(ActionEvent event) {
    }

    @FXML
    private void handleSettle(ActionEvent event) {
        Helper.createAlertConfirm("Confirm payment", "Settle Customer Balance", 
        "Are you sure to settle this customer balance", ()->{
            
        });
    }

    @FXML
    private void handleMakeBalance(ActionEvent event) {
        tfBalance.setText(tfOrdersPrice.getText());
    }

    private double parseDouble(String text) {
        if (text == null || text.isBlank()) {
            return 0.0;
        }
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
