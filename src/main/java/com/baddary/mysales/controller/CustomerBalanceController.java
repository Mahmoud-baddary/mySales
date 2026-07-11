package com.baddary.mysales.controller;

import com.baddary.mysales.dto.CustomerDTO;
import java.util.List;

import com.baddary.mysales.helper.Helper;
import com.baddary.mysales.mapper.CustomerMapper;
import com.baddary.mysales.row.CustomerBalanceRow;
import com.baddary.mysales.row.CustomerRow;
import com.baddary.mysales.service.CustomerApiService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Label;

public class CustomerBalanceController {
    @FXML
    private TextField tfSearch;
    @FXML
    private ComboBox<String> cbFilter;
    @FXML
    private Button btnSearch;
    @FXML
    private Button btnReset;
    @FXML
    private TableView<CustomerBalanceRow> tblCustomers;
    @FXML
    private TableColumn<CustomerBalanceRow, Number> colId;
    @FXML
    private TableColumn<CustomerBalanceRow, String> colName;
    @FXML
    private TableColumn<CustomerBalanceRow, Number> colBalance;
    @FXML
    private TableColumn<CustomerBalanceRow, String> colStatus;
    @FXML
    private Button btnSettle;
    @FXML
    private Button btnRefresh;
    @FXML
    private Label lblStatus;

    private final CustomerApiService customerApiService = new CustomerApiService();
    private final ObservableList<CustomerBalanceRow> customerRows = FXCollections.observableArrayList();

    public void initialize(Stage stage) {
        // set columns
        colId.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        colName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        colBalance.setCellValueFactory(cellData -> cellData.getValue().balanceProperty());
        colStatus.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        tblCustomers.setItems(customerRows);
        // Disable settle button if no selection
        btnSettle.disableProperty().bind(tblCustomers.getSelectionModel().selectedItemProperty().isNull());
        loadAllCustomers(stage);
    }

    @FXML
    private void handleSearch(ActionEvent event) {
    }

    @FXML
    private void handleReset(ActionEvent event) {
    }

    @FXML
    private void handleSettle(ActionEvent event) {
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
    }

    private void loadAllCustomers(Stage stage){
        Task<List<CustomerDTO>> allAsync = customerApiService.findAllAsync();
        Helper.startTask(allAsync, event->{
            customerRows.clear();
            customerRows.addAll(allAsync.getValue().stream().map(CustomerMapper::toBalanceRow).toList());
        }, null, stage);

    }
}
