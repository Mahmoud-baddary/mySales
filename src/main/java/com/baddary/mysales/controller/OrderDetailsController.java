package com.baddary.mysales.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import com.baddary.mysales.dto.OrderDTO;
import com.baddary.mysales.enums.OrderType;
import com.baddary.mysales.helper.Helper;
import com.baddary.mysales.mapper.OrderProductMapper;
import com.baddary.mysales.row.OrderProductSearchRow;
import com.baddary.mysales.service.OrderApiService;
import com.baddary.mysales.util.TokenStore;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import com.baddary.mysales.dto.ProductSummaryDTO;

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

    @FXML 
    private Button btnReturn;

    private final OrderApiService orderApiService = new OrderApiService();
    private final ObservableList<OrderProductSearchRow> orderItemsList = FXCollections.observableArrayList();
    private final ObservableList<String> units = FXCollections.observableArrayList();

    private Long originalOrderId, customerId;
    private Stage stage;
    private OrderType originalOrderType;
    private double originalOrderDiscount;

    public void intialize(Stage stage, long orderId) {
        setupTableColumns();
        tblOrderItems.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tblOrderItems.setItems(orderItemsList);
        this.originalOrderId = orderId;
        this.stage = stage;
        loadOrder();
        
    }

    private void loadOrder(){
        Task<OrderDTO> byIdAsync = orderApiService.findByIdAsync(this.originalOrderId);
        Helper.startTask(byIdAsync, e -> {
            OrderDTO dto = byIdAsync.getValue();
            customerId = dto.getCustomerId();
            lblCustomer.setText(dto.getCustomerName());
            lblDate.setText(dto.getDate().toString());
            lblDiscount.setText(String.valueOf(dto.getDiscount()));
            originalOrderDiscount = dto.getDiscount();
            lblOrderId.setText(String.valueOf(dto.getId()));
            lblPaidMoney.setText(String.valueOf(dto.getPaidMoney()));
            lblOrderType.setText(dto.getOrderType().name());
            this.originalOrderType = dto.getOrderType();
            lblTotalPrice.setText(String.valueOf(dto.calculateTotalPrice()));
            lblUser.setText(dto.getUserName());
            lblTime.setText(dto.getTime().toString());
            orderItemsList.clear();
            dto.getOrderProductDTOSet().forEach(op -> {
                orderItemsList.add(OrderProductMapper.toRow(op));

            });
            btnReturn.setDisable(originalOrderType != OrderType.BUY && originalOrderType != OrderType.SALE);
        }, null, this.stage);
    }

    @FXML
    private void handleClose(ActionEvent event) {
        this.stage.close();
    }

    @FXML
    private void handleReturn(ActionEvent event) throws IOException {
        ObservableList<OrderProductSearchRow> selectedItems = tblOrderItems.getSelectionModel().getSelectedItems();
        if (selectedItems.size() == 0) {
            Helper.createAlertError("No item selected", "Please selecte items you want to return").show();
            return;
        }
        OrderDTO returnOrderDTO = createReturnOrderDTO(selectedItems);
        double totalPrice = returnOrderDTO.calculateTotalPrice();
        Optional<String> result = Helper.createTextInputDialog(
                String.valueOf(totalPrice), "Payment", "Enter the amount paid", "Paid money")
                .showAndWait();
        result.ifPresent(input->{
            try{
                double paidMoney = Double.parseDouble(input.trim());
                if (paidMoney < 0) {
                    Helper.createAlertError("Error", "Amount cannot be negative.").show();
                    return;
                }
                if (paidMoney > totalPrice) {
                    Helper.createAlertError("Error", "paidMoney can't be greater than order price").show();
                    return;
                }
                returnOrderDTO.setPaidMoney(paidMoney);
                Task<OrderDTO> addOrderAsync = orderApiService.addOrderAsync(returnOrderDTO);
                Helper.startTask(addOrderAsync, e->{
                    Helper.createAlertInfo("Successful operation", "order has been saved successfully").show();
                    stage.close();
                }, null, stage);
            }catch (NumberFormatException e) {
                Helper.createAlertError("Error", "Please enter a valid number.").show();
            }
        });
    }

    private OrderDTO createReturnOrderDTO(List<OrderProductSearchRow> items) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setCustomerId(customerId);
        orderDTO.setOriginalOrderId(originalOrderId);
        orderDTO.setDate(LocalDate.now());
        orderDTO.setTime(LocalTime.now());
        orderDTO.setUserId(TokenStore.getCurrentUserId());
        orderDTO.setDiscount(originalOrderDiscount);
        orderDTO.setOrderType(originalOrderType == OrderType.BUY ? OrderType.BUY_RETURN : OrderType.SALE_RETURN);
        items.forEach(item -> {
            orderDTO.getOrderProductDTOSet().add(OrderProductMapper.toDTO(item));
        });
        return orderDTO;
    }

    private void setupTableColumns() {
        tblOrderItems.setEditable(true);
        colUnit.setEditable(true);
        colProductId.setCellValueFactory(cellData -> cellData.getValue().productIdProperty());
        colUnit.setCellValueFactory(cellData -> cellData.getValue().unitProperty());
        colQuantity.setCellValueFactory(cellData -> cellData.getValue().quantityProperty());
        colPrice.setCellValueFactory(cellData -> cellData.getValue().priceProperty());
        colDiscount.setCellValueFactory(cellData -> cellData.getValue().discountProperty());
        colTotal.setCellValueFactory(cellData -> cellData.getValue().totalPriceProperty()); // need totalProperty in
        colProductName.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getProductSummaryDTO().getName())); // OrderProductRow
        colExpireDate.setCellValueFactory(cellData -> cellData.getValue().expireDateProperty());
        colBatch.setCellValueFactory(cellData -> cellData.getValue().batchProperty());
        colNotes.setCellValueFactory(cellData -> cellData.getValue().noteProperty()); // noteProperty in OrderProductRow

        setupUnitCol();
        setupQuantityCol();
    }

    private void setupQuantityCol() {
        colQuantity.setEditable(true);
        colQuantity.setCellFactory(TextFieldTableCell.forTableColumn(new NumberStringConverter()));
        // quantity must be positive and <= stock
        colQuantity.setOnEditCommit(event -> {
            OrderProductSearchRow row = event.getRowValue();
            double newValue = event.getNewValue().doubleValue();
            double oldValue = event.getOldValue().doubleValue();
            if (newValue <= 0) {
                return;
            }
            row.setQuantity(newValue);
            if (row.getQuantitySU() > row.getOriginalQuantitySU()) {
                row.setQuantity(oldValue);
            }
            tblOrderItems.refresh();
            tblOrderItems.requestFocus();
        });
    }

    private void setupUnitCol() {
        colUnit.setCellFactory(ChoiceBoxTableCell.forTableColumn(units));
        // prepare units list according to row
        colUnit.setOnEditStart(event -> {
            units.clear();
            ProductSummaryDTO dto = event.getRowValue().getProductSummaryDTO();

            units.add(dto.getGreatestUnit());
            units.add(dto.getMediumUnit());
            units.add(dto.getSmallestUnit());

        });
        colUnit.setOnEditCommit(event -> {
            OrderProductSearchRow row = event.getRowValue();
            String oldUnit = event.getOldValue();
            row.setUnit(event.getNewValue());
            double newQuantitySU = row.getQuantitySU();
            // make sure that quantity is not bigger than old value
            if (newQuantitySU > row.getOriginalQuantitySU()) {
                row.setUnit(oldUnit);
            }
            tblOrderItems.refresh();
            tblOrderItems.requestFocus();
        });
    }

}
