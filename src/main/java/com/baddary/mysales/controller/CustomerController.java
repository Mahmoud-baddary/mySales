package com.baddary.mysales.controller;

import java.util.List;
import java.util.Optional;

import com.baddary.mysales.dto.CustomerDTO;
import com.baddary.mysales.helper.Helper;
import com.baddary.mysales.mapper.CustomerMapper;
import com.baddary.mysales.mapper.PhoneMapper;
import com.baddary.mysales.row.CustomerRow;
import com.baddary.mysales.row.PhoneRow;
import com.baddary.mysales.service.CustomerApiService;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;

public class CustomerController {
    // private static final Logger log =
    // LoggerFactory.getLogger(CustomerController.class);
    @FXML
    private Button btnAdd;
    @FXML
    private Button btnUpdate;
    @FXML
    private Label lblStatus;
    @FXML
    private TextField tfName;
    @FXML
    private TextField tfEmail;
    @FXML
    private TextArea taAddress;
    @FXML
    private TableView<PhoneRow> tblPhone;
    @FXML
    private TableColumn<PhoneRow, String> colPhoneNumber;
    @FXML
    private TextField tfPhoneNumber;
    @FXML
    private TextField tfSearchByName;
    @FXML
    private TableView<CustomerRow> tblCustomer;
    @FXML
    private TableColumn<CustomerRow, String> colCustomerName;
    @FXML
    private TextField tfCustomerId;
    private final ObservableList<PhoneRow> phoneRows = FXCollections.observableArrayList();
    private final ObservableList<CustomerRow> customerRows = FXCollections.observableArrayList();
    private final CustomerApiService customerApiService = new CustomerApiService();
    private Stage stage;

    public void intialize(Stage stage){
        this.stage = stage;
        colPhoneNumber.setCellValueFactory(cellData -> cellData.getValue().numberProperty());
        colCustomerName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        tblPhone.setItems(phoneRows);
        tblCustomer.setItems(customerRows);

        // get all customers from db and add them to customers of the table
        loadAllCustomers();
        // formatter to prevent anything other than numbers
        TextFormatter<String> formatter = new TextFormatter<>(change -> {
            if (change.getControlNewText().matches("[\\d\\s\\-()]*")) {
                return change;
            }
            return null; // Reject the change
        });
        tfPhoneNumber.setTextFormatter(formatter);

        // btn add and update enable and disable acc. to item is selected
        btnUpdate.disableProperty().bind(tblCustomer.getSelectionModel().selectedItemProperty().isNull());
        btnAdd.disableProperty().bind(tblCustomer.getSelectionModel().selectedItemProperty().isNotNull());
        // on item selected from the table
        tblCustomer.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        phoneRows.clear();
                        Task<Optional<CustomerDTO>> task = customerApiService
                                .findByExactNameAsync(newValue.getName());
                        Helper.startTask(task, ev -> {
                            task.getValue().ifPresent(dto -> {
                                tfName.setText(dto.getName());
                                tfCustomerId.setText(String.valueOf(dto.getId()));
                                tfEmail.setText(dto.getEmail());
                                tfCustomerId.setText(String.valueOf(dto.getId()));
                                taAddress.setText(dto.getAddress());
                                phoneRows.addAll(dto.getPhoneDTOSet().stream().map(PhoneMapper::toRow).toList());

                            });
                        }, null, this.stage);

                    }
                });
        // on search
        tfSearchByName.textProperty().addListener(
                (observable, oldValue, newValue) -> {
                    Timeline debouncer = new Timeline(new KeyFrame(Duration.millis(300),
                            event -> {
                                if (newValue == null || newValue.isBlank()) {
                                    loadAllCustomers();
                                    return;
                                }
                                Task<List<CustomerDTO>> task = customerApiService.searchByName(newValue);
                                Helper.startTask(task, e -> {
                                    List<CustomerDTO> dtos = task.getValue();
                                    customerRows.clear();
                                    customerRows.addAll(dtos.stream().map(CustomerMapper::toRow).toList());
                                }, null, this.stage);

                            }));
                    debouncer.setCycleCount(1);
                    debouncer.play();
                });
    }

   
    public void handleAddPhone(ActionEvent event) {
        if (tfPhoneNumber.getText().isBlank())
            return;
        PhoneRow row = new PhoneRow();
        row.setNumber(tfPhoneNumber.getText());
        phoneRows.add(row);
    }

    public void handleRemovePhone(ActionEvent event) {
        PhoneRow row = tblPhone.getSelectionModel().getSelectedItem();
        if (row != null) {
            phoneRows.remove(row);
        }
    }

    public void handleNewCustomer(ActionEvent event) {
        clear();
    }

    private void loadAllCustomers() {
        Task<List<CustomerDTO>> task = customerApiService.findAllAsync();
        Helper.startTask(task, event -> {
            customerRows.clear();
            customerRows.addAll(task.getValue().stream().map(CustomerMapper::toRow).toList());
        }, null, this.stage);
    }

    public void handleAddCustomer(ActionEvent event) {
        if (isEmpty()) {
            lblStatus.setText("Name field mustn't be empty");
            return;
        }

        CustomerDTO dto = new CustomerDTO();
        dto.setName(tfName.getText());
        dto.setEmail(tfEmail.getText());
        dto.setAddress(taAddress.getText());
        dto.getPhoneDTOSet().addAll(phoneRows.stream().map(PhoneMapper::toDTO).toList());

        Task<CustomerDTO> task = customerApiService.addCustomerAsync(dto);
        Helper.startTask(task, e -> {
            lblStatus.setText("Customer added successfully");
            clear();
            loadAllCustomers(); // refresh the table
        }, null, this.stage);

    }

    public boolean isEmpty() {
        return tfName.getText().isBlank();

    }

    public void clear() {
        phoneRows.clear();
        tfName.clear();
        tfEmail.clear();
        taAddress.clear();
        tfPhoneNumber.clear();
        tfCustomerId.clear();

    }

    public void handleUpdateCustomer(ActionEvent event) {
        CustomerRow row = tblCustomer.getSelectionModel().getSelectedItem();
        if (row != null && !tfName.getText().isBlank()) {
            String name = tfName.getText();
            String email = tfEmail.getText();
            Long id = Long.valueOf(tfCustomerId.getText());
            String address = taAddress.getText();
            CustomerDTO dto = new CustomerDTO();
            dto.setName(name);
            dto.setAddress(address);
            dto.setId(id);
            dto.setEmail(email);
            dto.getPhoneDTOSet().addAll(phoneRows.stream().map(PhoneMapper::toDTO).toList());
            Task<CustomerDTO> task = customerApiService.updateCustomerAsync(dto.getId(), dto);
            Helper.startTask(task, e -> {
                lblStatus.setText("Customer updated successfully");
                clear();
                loadAllCustomers();
            }, null, this.stage);

        } else {
            Helper.createAlertError("Error", "No customer is selected").show();
        }
    }

}
