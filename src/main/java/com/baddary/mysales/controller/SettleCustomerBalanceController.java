package com.baddary.mysales.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ComboBox;

import java.time.LocalDate;
import java.util.List;

import com.baddary.mysales.dto.CustomerDTO;
import com.baddary.mysales.dto.OrderDTO;
import com.baddary.mysales.enums.OrderType;
import com.baddary.mysales.helper.Helper;
import com.baddary.mysales.mapper.OrderMapper;
import com.baddary.mysales.row.OrderProductSaleRow;
import com.baddary.mysales.row.OrderSearchRow;
import com.baddary.mysales.service.OrderApiService;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
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
    private TableColumn<OrderSearchRow, String> colOrderType;
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
    private TextField tfTotal;
    @FXML
    private TextField tfDiscount;
    @FXML
    private TextField tfTotalAfterDiscount;
    @FXML
    private Button btnSettle;

    private Stage stage;
    private long customerId;

    private OrderApiService orderApiService = new OrderApiService();
    private final ObservableList<OrderSearchRow> orderRows = FXCollections.observableArrayList();

    public void intialize(Stage stage, CustomerDTO customerDTO) {
        this.stage = stage;
        this.customerId = customerDTO.getId();
        tfCustomerName.setText(customerDTO.getName());
        tfBalance.setText(String.valueOf(customerDTO.getBalance()));
        tfTotal.setText(String.valueOf(customerDTO.getBalance()));
        cbOrderType.getItems().addAll("ALL", OrderType.BUY.name(), OrderType.SALE.name());

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
            orderRows.addAll(orders.stream().map(OrderMapper::toRow).toList());
        }, null, this.stage);

        DoubleBinding totalBinding = Bindings.createDoubleBinding(
                () -> orderRows.stream().mapToDouble(OrderSearchRow::getFinalPrice).sum(), orderRows);

        tfTotal.textProperty().bind(totalBinding.asString("%.2f"));

        DoubleProperty discountProperty = new SimpleDoubleProperty(0);
        tfDiscount.textProperty().bindBidirectional(discountProperty, new NumberStringConverter());

        DoubleBinding totalAfterDiscountBinding = totalBinding.multiply(
                discountProperty.divide(100).negate().add(1));

        tfTotalAfterDiscount.textProperty().bind(
                totalAfterDiscountBinding.asString("%.2f"));
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        loadOrders(this.customerId);
    }

    @FXML
    private void handleReset(ActionEvent event) {
        
    }

    @FXML
    private void handleViewDetails(ActionEvent event) {
    }

    @FXML
    private void handleClose(ActionEvent event) {
    }

    @FXML
    private void handleSettle(ActionEvent event) {
    }
}
