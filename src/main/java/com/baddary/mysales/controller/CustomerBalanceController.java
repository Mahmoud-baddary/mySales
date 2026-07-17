package com.baddary.mysales.controller;

import com.baddary.mysales.dto.CustomerDTO;
import com.baddary.mysales.enums.CustomerStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.baddary.mysales.helper.Helper;
import com.baddary.mysales.mapper.CustomerMapper;
import com.baddary.mysales.row.CustomerBalanceRow;
import com.baddary.mysales.service.CustomerApiService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
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
    private Stage stage;

    public void initialize(Stage stage) {
        this.stage = stage;
        // set columns
        colId.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        colName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        colBalance.setCellValueFactory(cellData -> cellData.getValue().balanceProperty());
        colStatus.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        tblCustomers.setItems(customerRows);
        // Disable settle button if no selection
        btnSettle.disableProperty().bind(tblCustomers.getSelectionModel().selectedItemProperty().isNull());
        loadAllCustomers();
        cbFilter.getItems().addAll("ALL", CustomerStatus.OWES.name(),
                CustomerStatus.DESERVES.name(), CustomerStatus.SETTLED.name());
        cbFilter.getSelectionModel().select("ALL");

    }

    @FXML
    private void handleSearch(ActionEvent event) {
        String name = tfSearch.getText().trim();
        String status = cbFilter.getValue();
        Task<List<CustomerDTO>> searchAsync = customerApiService.searchByNameAndBalanceStatusAsync(name, status);
        Helper.startTask(searchAsync, e -> {
            List<CustomerDTO> customers = searchAsync.getValue();
            customerRows.clear();
            customerRows.addAll(customers.stream().map(CustomerMapper::toBalanceRow).toList());
        }, null, this.stage);
    }

    @FXML
    private void handleReset(ActionEvent event) {
        tfSearch.clear();
        cbFilter.getSelectionModel().select("ALL");
        loadAllCustomers();
    }

    @FXML
    private void handleSettle(ActionEvent event) {
        CustomerBalanceRow selected = tblCustomers.getSelectionModel().getSelectedItem();
        if (selected == null) {
            lblStatus.setText("No customer selected.");
            return;
        }
        showSettleDialog(selected);
    }

    

    @FXML
    private void handleRefresh(ActionEvent event) {
    }

    private void loadAllCustomers() {
        Task<List<CustomerDTO>> allAsync = customerApiService.findAllAsync();
        Helper.startTask(allAsync, event -> {
            customerRows.clear();
            customerRows.addAll(allAsync.getValue().stream().map(CustomerMapper::toBalanceRow).toList());
        }, null, this.stage);

    }

    private void showSettleDialog(CustomerBalanceRow selected) {
        double balance = Math.abs(selected.getBalance());
        String labelText;
        String actionText;
        if (selected.getStatus().equals(CustomerStatus.OWES.name())) {
            labelText = "Customer owes you " + balance + ". How much do you want to receive?";
            actionText = "Receive Payment";
        } else if (selected.getStatus().equals(CustomerStatus.DESERVES.name())) {
            labelText = "You owe customer " + balance + ". How much do you want to pay?";
            actionText = "Pay Customer";
        } else {
            Helper.createAlertInfo("Zero Balance", "This customer's balance is already settled.").show();
            return;
        }

        // Use TextInputDialog for amount input
        TextInputDialog dialog = new TextInputDialog(String.valueOf(balance));
        dialog.setTitle("Settle Balance");
        dialog.setHeaderText(labelText);
        dialog.setContentText("Amount:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(input -> {
            try {
                double amount = Double.parseDouble(input.trim());
                if (amount <= 0) {
                    Helper.createAlertError("Error", "Amount must be positive.").show();
                    return;
                }
                // Ensure amount does not exceed absolute balance (optional)
                if (amount > balance) {
                    Helper.createAlertError("Error", "Amount cannot exceed the balance.").show();
                    return;
                }
                // Call API to update customer balance
                Task<CustomerDTO> settleBalanceTask = customerApiService.settleCustomerBalanceAsync(selected.getId(), amount);
                Helper.startTask(settleBalanceTask, e->{
                    loadAllCustomers();
                }, null, this.stage);
            } catch (NumberFormatException e) {
                Helper.createAlertError("Error", "Invalid amount entered.").show();
            }
        });
    }

}
