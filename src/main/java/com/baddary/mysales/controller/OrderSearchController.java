package com.baddary.mysales.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.DatePicker;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import com.baddary.mysales.MainApplication;
import com.baddary.mysales.dto.OrderDTO;
import com.baddary.mysales.enums.OrderType;
import com.baddary.mysales.enums.PaymentType;
import com.baddary.mysales.helper.Helper;
import com.baddary.mysales.mapper.OrderMapper;
import com.baddary.mysales.row.OrderSearchRow;
import com.baddary.mysales.service.OrderApiService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.ComboBox;

public class OrderSearchController {

    @FXML
    private TextField tfCustomerName;
    @FXML
    private TextField tfProductName;
    @FXML
    private TextField tfUserName;
    @FXML
    private DatePicker dpFromDate;
    @FXML
    private DatePicker dpToDate;
    @FXML
    private Button btnSearch;
    @FXML
    private Button btnReset;
    @FXML
    private Button btnViewDetails;
    @FXML
    private Button btnClose;
    @FXML
    private TableView<OrderSearchRow> tblOrders;
    @FXML
    private TableColumn<OrderSearchRow, Number> colOrderId;
    @FXML
    private TableColumn<OrderSearchRow, LocalDate> colOrderDate;
    @FXML
    private TableColumn<OrderSearchRow, String> colCustomerName;
    @FXML
    private TableColumn<OrderSearchRow, String> colProductNames;
    @FXML
    private TableColumn<OrderSearchRow, Number> colFinalPrice;
    @FXML
    private TableColumn<OrderSearchRow, String> colUser;
    @FXML
    private TableColumn<OrderSearchRow, OrderType> colOrderType;
    @FXML
    private TableColumn<OrderSearchRow, Number> colPaidMoney;
    @FXML
    private ComboBox<String> cbOrderType;
    @FXML
    private ComboBox<String> cbPaymentType;

    private final OrderApiService orderApiService = new OrderApiService();
    private final ObservableList<OrderSearchRow> orderRows = FXCollections.observableArrayList();
    private Stage stage;

    public void intialize(Stage stage) {
        this.stage = stage;
        cbOrderType.getItems().addAll("All", "SALE", "BUY");
        cbOrderType.getSelectionModel().select("All");

        cbPaymentType.getItems().addAll("All", "INSTANT", "DEFERRED");
        cbPaymentType.getSelectionModel().select("All");
        colOrderId.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        colOrderDate.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
        colCustomerName.setCellValueFactory(cellData -> cellData.getValue().customerNameProperty());
        colOrderType.setCellValueFactory(cellData -> cellData.getValue().orderTypeProperty());
        colPaidMoney.setCellValueFactory(cellData -> cellData.getValue().paidMoneyProperty());
        colFinalPrice.setCellValueFactory(cellData -> cellData.getValue().finalPriceProperty());
        colUser.setCellValueFactory(cellData -> cellData.getValue().userNameProperty());
        tblOrders.setItems(orderRows);

        // // Double-click row to show details
        // tblOrders.setRowFactory(tv -> {
        // TableRow<OrderSearchRow> row = new TableRow<>();
        // row.setOnMouseClicked(event -> {
        // if (event.getClickCount() == 2 && !row.isEmpty()) {
        // OrderSearchRow selected = row.getItem();
        // try {
        // showOrderDetails(selected.getId());
        // } catch (IOException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        // }
        // });
        // return row;
        // });

        // Set default combo box selections
        cbOrderType.getSelectionModel().select("All");
        cbPaymentType.getSelectionModel().select("All");
    }

    
    private void showOrderDetails(long id) throws IOException {
        //
        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("order_details_dialog.fxml"));
        Parent root = loader.load();
        Stage stage1 = new Stage();
        OrderDetailsController controller = loader.getController();
        controller.intialize(stage1, id);
        stage1.setTitle("Order details");
        stage1.setScene(new Scene(root));
        stage1.setMinHeight(600);
        stage1.setMinWidth(900);
        stage1.sizeToScene();
        stage1.show();
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        String customerName = tfCustomerName.getText().isBlank() ? null : tfCustomerName.getText();
        String productName = tfProductName.getText().isBlank() ? null : tfProductName.getText();
        String userName = tfUserName.getText().isBlank() ? null : tfUserName.getText();
        LocalDate fromDate = dpFromDate.getValue();
        LocalDate toDate = dpToDate.getValue();

        String orderTypeStr = cbOrderType.getValue();
        OrderType orderType = null;
        if (orderTypeStr != null && !"All".equals(orderTypeStr)) {
            orderType = OrderType.valueOf(orderTypeStr);
        }

        String paymentTypeStr = cbPaymentType.getValue();
        PaymentType paymentType = null;
        if (paymentTypeStr != null && !"All".equals(paymentTypeStr)) {
            paymentType = PaymentType.valueOf(paymentTypeStr);
        }

        Task<List<OrderDTO>> searchTask = orderApiService.searchOrdersAsync(
                customerName, productName, userName, fromDate, toDate, orderType,
                paymentType);
        Helper.startTask(searchTask, e -> {
            orderRows.clear();
            List<OrderDTO> dtos = searchTask.getValue();
            orderRows.addAll(dtos.stream().map(OrderMapper::toRow).toList());

        }, null, this.stage);

    }

    @FXML
    private void handleReset(ActionEvent event) {
        tfCustomerName.clear();
        tfProductName.clear();
        tfUserName.clear();
        dpFromDate.setValue(null);
        dpToDate.setValue(null);
        cbOrderType.getSelectionModel().select("All");
        cbPaymentType.getSelectionModel().select("All");
    }

    @FXML
    private void handleViewDetails(ActionEvent event) throws IOException {
        OrderSearchRow selected = tblOrders.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Helper.createAlertError("No selection", "Please select an order first.").show();
            return;
        }
        showOrderDetails(selected.getId());
    }

    @FXML
    private void handleClose(ActionEvent event) {
        this.stage.close();
    }
}
