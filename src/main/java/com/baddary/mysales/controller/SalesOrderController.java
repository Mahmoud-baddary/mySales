package com.baddary.mysales.controller;

import com.baddary.mysales.MainApplication;
import com.baddary.mysales.dto.CustomerDTO;
import com.baddary.mysales.dto.OrderDTO;
import com.baddary.mysales.dto.OrderProductDTO;
import com.baddary.mysales.dto.ProductDTO;
import com.baddary.mysales.dto.StockDTO;
import com.baddary.mysales.enums.OrderType;
import com.baddary.mysales.helper.Helper;
import com.baddary.mysales.mapper.OrderProductMapper;
import com.baddary.mysales.row.OrderProductSaleRow;
import com.baddary.mysales.service.CustomerApiService;
import com.baddary.mysales.service.OrderApiService;
import com.baddary.mysales.service.ProductApiService;
import com.baddary.mysales.service.StockApiService;
import com.baddary.mysales.util.TokenStore;

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class SalesOrderController {
    @FXML
    private TableColumn<OrderProductSaleRow, Number> colStock;
    @FXML
    private TableColumn<OrderProductSaleRow, String> colNotes;
    @FXML
    private TableColumn<OrderProductSaleRow, Number> colDiscount;
    @FXML
    private TableColumn<OrderProductSaleRow, String> colBatch;
    @FXML
    private TableColumn<OrderProductSaleRow, LocalDate> colExpire;
    @FXML
    private TextField tfCustomerName;
    @FXML
    private TextField tfPhoneNumber;
    @FXML
    private TextArea taAddress;
    @FXML
    private TableView<OrderProductSaleRow> tblProducts;
    @FXML
    private TableColumn<OrderProductSaleRow, Number> colProductId;
    @FXML
    private TableColumn<OrderProductSaleRow, Number> colQuantity;
    @FXML
    private TableColumn<OrderProductSaleRow, String> colProductName;
    @FXML
    private TableColumn<OrderProductSaleRow, Number> colPrice;
    @FXML
    private TableColumn<OrderProductSaleRow, Number> colTotal;
    @FXML
    private Label lblTotalPrice;
    @FXML
    private TextField tfDiscount;
    @FXML
    private Label lblFinalPrice;
    @FXML
    private Label lblStatus;
    @FXML
    private TableColumn<OrderProductSaleRow, String> colUnit;
    @FXML
    private TextField tfEmail;
    @FXML
    private TextField tfCustomerId;

    private final ObservableList<String> units = FXCollections.observableArrayList();
    private final ObservableList<LocalDate> expires = FXCollections.observableArrayList();
    private final ObservableList<OrderProductSaleRow> saleRows = FXCollections.observableArrayList(
            // to tell javafx that salesrows changed so fire the binding that wait for
            // change in it
            op -> new Observable[] {
                    op.totalPriceProperty()
            });
    private final CustomerApiService customerApiService = new CustomerApiService();
    private final OrderApiService orderApiService = new OrderApiService();
    private final ProductApiService productApiService = new ProductApiService();
    private final StockApiService stockApiService = new StockApiService();
    private Stage stage;

    public void initialize(Stage stage) {
        this.stage = stage;
        prepareTableCells();
        setupFocusSeries();
        tblProducts.setItems(saleRows);

        // formatter to prevent anything other than numbers
        TextFormatter<String> phoneNumFormatter = new TextFormatter<>(change -> {
            if (change.getControlNewText().matches("[\\d\\s\\-()]*")) {
                return change;
            }
            return null; // Reject the change
        });
        tfPhoneNumber.setTextFormatter(phoneNumFormatter);
        // fill fields when typing phone num
        tfPhoneNumber.setOnAction(event -> {
            Task<Optional<CustomerDTO>> byPhoneAsync = customerApiService
                    .findByPhoneAsync(tfPhoneNumber.getText().strip());
            Helper.startTask(byPhoneAsync, e -> {
                Optional<CustomerDTO> optionalCustomer = byPhoneAsync.getValue();
                optionalCustomer.ifPresentOrElse(dto -> {
                    tfPhoneNumber.getStyleClass().remove("text-field-error");
                    tfEmail.setText(dto.getEmail());
                    tfCustomerId.setText(String.valueOf(dto.getId()));
                    tfCustomerName.setText(dto.getName());
                    taAddress.setText(dto.getAddress());
                    tfDiscount.requestFocus();
                }, () -> {
                    tfPhoneNumber.getStyleClass().add("text-field-error");
                    java.awt.Toolkit.getDefaultToolkit().beep();
                });
            }, null, stage);

        });

        // make discount get only numbers
        TextFormatter<Number> discountFormatter = new TextFormatter<>(
                new NumberStringConverter(), 0.0, change -> {
                    String text = change.getControlNewText();
                    if (text.isEmpty())
                        return change;
                    try {
                        Double.parseDouble(text);
                        return change;
                    } catch (Exception e) {
                        return null;
                    }
                });
        tfDiscount.setTextFormatter(discountFormatter);

        // make sure that no duplicate
        saleRows.addListener(new ListChangeListener<OrderProductSaleRow>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends OrderProductSaleRow> c) {
                while (c.next()) {

                    if (c.wasAdded()) {

                        List<OrderProductSaleRow> duplicates = new ArrayList<>();

                        for (OrderProductSaleRow op : c.getAddedSubList()) {
                            if (isProductExist(op)) {
                                duplicates.add(op);
                            }
                        }

                        if (!duplicates.isEmpty()) {
                            Helper.createAlertError(
                                    "Error",
                                    "This product was already added with the same expire date").show();

                            // 🔥 MODIFY OUTSIDE CHANGE ITERATION
                            Platform.runLater(() -> saleRows.removeAll(duplicates));
                        }

                        // UI selection (safe)
                        Platform.runLater(() -> {
                            int row = saleRows.size() - 1;
                            if (row >= 0) {
                                tblProducts.scrollTo(row);
                                tblProducts.getSelectionModel().select(row);
                            }
                        });
                    }
                }
            }
        });
        //
        DoubleBinding totalPriceBinding = Bindings.createDoubleBinding(
                () -> saleRows.stream().mapToDouble(OrderProductSaleRow::getTotalPrice).sum(), saleRows);

        lblTotalPrice.textProperty().bind(totalPriceBinding.asString("%.2f"));

        DoubleProperty discountProperty = new SimpleDoubleProperty(0);
        tfDiscount.textProperty().bindBidirectional(discountProperty, new NumberStringConverter());

        DoubleBinding finalPriceBinding = totalPriceBinding.multiply(
                discountProperty.divide(100).negate().add(1));

        lblFinalPrice.textProperty().bind(
                finalPriceBinding.asString("%.2f"));
    }

    public void handleAddProduct(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("sales_add_product_view.fxml"));
        Parent root = loader.load();
        Stage stage1 = new Stage();
        SalesAddProductController controller = loader.getController();
        controller.initialize(stage1, saleRows);
        stage1.initOwner(this.stage);
        stage1.initModality(Modality.APPLICATION_MODAL);
        stage1.setTitle("Search for product");
        stage1.setMinHeight(600);
        stage1.setMinWidth(600);
        stage1.sizeToScene();
        stage1.setScene(new Scene(root, 400, 300));
        stage1.show();
    }

    public void handleRemoveProduct(ActionEvent event) {
        OrderProductSaleRow op = tblProducts.getSelectionModel().getSelectedItem();
        if (op != null) {
            saleRows.remove(op);
        }
    }

    public void handleSaveOrder(ActionEvent event) {
        // get required info
        String customerIdString = tfCustomerId.getText();
        if (customerIdString.isBlank()) {
            lblStatus.setText("Select customer");
            return;
        }
        Long customerId = Long.parseLong(customerIdString);
        if (saleRows.isEmpty()) {
            lblStatus.setText("No products were added");
            return;
        }

        OrderDTO orderDTO = new OrderDTO();
        TextInputDialog dialog = new TextInputDialog(lblFinalPrice.getText());
        dialog.setTitle("Payment");
        dialog.setHeaderText("Enter the amount paid by the customer:");
        dialog.setContentText("Paid Money:");

        // Show the dialog and wait for user response
        Optional<String> result = dialog.showAndWait();

        result.ifPresent(input -> {
            try {
                double paidMoney = Double.parseDouble(input.trim());
                if (paidMoney < 0) {
                    Helper.createAlertError("Error", "Amount cannot be negative.").show();
                    return;
                }
                if (paidMoney > Double.parseDouble(lblFinalPrice.getText())) {
                    Helper.createAlertError("Error", "paidMoney can't be greater than order price").show();
                    return;
                }
                // Set the paid money in your DTO
                orderDTO.setPaidMoney(paidMoney);
                // Proceed to save the order...
                LocalDate date = LocalDate.now();
                LocalTime time = LocalTime.now();
                double discount = Double.parseDouble(tfDiscount.getText());

                orderDTO.setCustomerId(customerId);
                orderDTO.setUserId(TokenStore.getCurrentUserId());
                orderDTO.setTime(time);
                orderDTO.setDiscount(discount);
                orderDTO.setDate(date);
                orderDTO.setOrderType(OrderType.SALE);
                List<OrderProductDTO> orderProductDTOS = saleRows.stream().map(OrderProductMapper::toDTO).toList();
                orderDTO.getOrderProductDTOSet().addAll(orderProductDTOS);

                Helper.startTask(orderApiService.addOrderAsync(orderDTO),
                        e -> {
                            saleRows.clear();
                            lblFinalPrice.setText("saved");
                        }, null, this.stage);
            } catch (NumberFormatException e) {
                Helper.createAlertError("Error", "Please enter a valid number.").show();
            }
        });

    }

    public void prepareTableCells() {
        colProductId.setCellValueFactory(
                cellData -> cellData.getValue().productIdProperty());

        colUnit.setCellFactory(ChoiceBoxTableCell.forTableColumn(units));
        colUnit.setCellValueFactory(cellData -> cellData.getValue().unitProperty());
        // prepare units list according to row
        colUnit.setOnEditStart(event -> {
            units.clear();
            Task<Optional<ProductDTO>> byIdAsync = productApiService.findByIdAsync(event.getRowValue().getProductId());
            Helper.startTask(byIdAsync, e -> {
                byIdAsync.getValue().ifPresent(dto -> {
                    units.add(dto.getGreatestUnit());
                    units.add(dto.getMediumUnit());
                    units.add(dto.getSmallestUnit());
                });
            }, null, this.stage);

        });
        colUnit.setOnEditCommit(event -> {
            OrderProductSaleRow row = event.getRowValue();
            row.setUnit(event.getNewValue());
            // make sure that quantity is not bigger than stock
            double quantity = row.getQuantity();
            double stock = row.getStock();
            if (quantity > stock) {
                row.setQuantity(stock);
            }
            tblProducts.refresh();
            tblProducts.requestFocus();
        });

        colProductName.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getProductDTO().getName()));

        colStock.setCellValueFactory(cellData -> cellData.getValue().stockProperty());

        colPrice.setCellValueFactory(cellData -> cellData.getValue().priceProperty());

        colExpire.setCellValueFactory(cellData -> cellData.getValue().expireDateProperty());
        colExpire.setCellFactory(ChoiceBoxTableCell.forTableColumn(expires));
        colExpire.setOnEditCommit(event -> {
            LocalDate expire = event.getNewValue();
            LocalDate oldExpire = event.getOldValue();
            OrderProductSaleRow row = event.getRowValue();
            row.setExpireDate(expire);
            // prevent duplication of product and expire
            if (isProductExist(row)) {
                Helper.createAlertError("Error", "the product is already exist with the same expire").show();
                // rollback
                row.setExpireDate(oldExpire);
                tblProducts.refresh();
                tblProducts.requestFocus();
                return;
            }
            // update values of price and stock when expire change
            Task<Optional<StockDTO>> stockByProductAndExpireAsync = stockApiService
                    .findStockByProductAndExpireAsync(row.getProductId(), row.getExpireDate());
            Helper.startTask(stockByProductAndExpireAsync, e -> {
                stockByProductAndExpireAsync.getValue().ifPresent(stockDTO -> {
                    row.setPriceSU(stockDTO.getPriceSU());
                    row.setStockSU(stockDTO.getQuantitySU());
                    // make sure that quantity is not bigger than stock
                    double quantity = row.getQuantity();
                    double stockDouble = row.getStock();
                    if (quantity > stockDouble) {
                        row.setQuantity(stockDouble);
                    }
                });
            }, null, this.stage);
            tblProducts.refresh();
            tblProducts.requestFocus();

        });

        colExpire.setOnEditStart(event -> {
            expires.clear();
            Long id = event.getRowValue().getProductId();
            Task<List<StockDTO>> stocksAsync = stockApiService.findStocksAsync(id);
            Helper.startTask(stocksAsync, e -> {
                stocksAsync.getValue().forEach(stock -> expires.add(stock.getExpire()));
            }, null, this.stage);

        });

        colTotal.setCellValueFactory(cellData -> cellData.getValue().totalPriceProperty());

        colQuantity.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getQuantity()));
        colQuantity.setEditable(true);
        colQuantity.setCellFactory(TextFieldTableCell.forTableColumn(new NumberStringConverter()));
        // quantity must be positive and <= stock
        colQuantity.setOnEditCommit(event -> {
            OrderProductSaleRow row = event.getRowValue();
            double value = event.getNewValue().doubleValue();
            if (value > 0 && value <= row.getStock()) {
                row.setQuantity(value);
            } else {
                row.setQuantity(event.getOldValue().doubleValue());
            }
            tblProducts.requestFocus();
        });
        colBatch.setCellValueFactory(cellData -> cellData.getValue().batchProperty());
        colNotes.setCellValueFactory(cellData -> cellData.getValue().noteProperty());
        colNotes.setCellFactory(TextFieldTableCell.forTableColumn());
        colNotes.setOnEditCommit(event -> tblProducts.requestFocus());
    }

    public void handleMenuManageCustomers(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("customer_view.fxml"));
        Parent root = loader.load();
        Stage stage1 = new Stage();
        stage1.initOwner(this.stage);
        stage1.initModality(Modality.APPLICATION_MODAL);
        stage1.setTitle("Add Customer");
        CustomerController controller = loader.getController();
        controller.intialize(stage1);
        stage1.setScene(new Scene(root));
        stage1.setMinHeight(600);
        stage1.setMinWidth(900);
        stage1.sizeToScene();
        stage1.show();

    }

    public void handleMenuManageProducts(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("product_view.fxml"));
        Parent root = loader.load();
        Stage stage1 = new Stage();
        stage1.initOwner(this.stage);
        stage1.initModality(Modality.APPLICATION_MODAL);
        stage1.setTitle("Manage Products");
        ProductController controller = loader.getController();
        controller.intialize(stage1);
        stage1.setScene(new Scene(root));
        stage1.setMinHeight(600);
        stage1.setMinWidth(900);
        stage1.sizeToScene();
        stage1.show();

    }

    public void handleMenuPurchaseOrder(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("purchase_order_view.fxml"));
        Parent root = loader.load();
        this.stage.setTitle("Purchase Order");
        PurchaseOrderController controller = loader.getController();
        controller.initialize(stage);
        Scene scene = new Scene(root);
        Helper.loadStyle(scene);
        this.stage.setScene(scene);
        this.stage.sizeToScene();

    }

    private void setupFocusSeries() {
        Platform.runLater(() -> {
            tfPhoneNumber.requestFocus();
        });
        tblProducts.onKeyPressedProperty().set(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                tfDiscount.requestFocus();
            }
        });
    }

    private boolean isProductExist(OrderProductSaleRow row) {
        return this.saleRows.stream()
                .filter(op -> Objects.equals(op.getProductId(), row.getProductId()))
                .filter(op -> op.getExpireDate().equals(row.getExpireDate()))
                .count() > 1;
    }

    public void handleMenuMyStock(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("stock_view.fxml"));
        Parent root = loader.load();
        Stage stage1 = new Stage();
        stage1.setTitle("Stock");
        stage1.initOwner(this.stage);
        stage1.initModality(Modality.APPLICATION_MODAL);
        Scene scene = new Scene(root);
        Helper.loadStyle(scene);
        StockController controller = loader.getController();
        controller.initialize(stage1);
        stage1.setScene(scene);
        stage1.setMinHeight(500);
        stage1.setMinWidth(700);
        stage1.sizeToScene();
        stage1.show();

    }

    @FXML
    private void handleMenuSearchOrders(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("order_search_view.fxml"));
        Parent root = loader.load();
        Stage stage1 = new Stage();
        stage1.setTitle("Search Orders");
        stage1.initOwner(this.stage);
        stage1.initModality(Modality.APPLICATION_MODAL);
        Scene scene = new Scene(root);
        Helper.loadStyle(scene);
        OrderSearchController controller = loader.getController();
        controller.intialize(stage1);
        stage1.setScene(scene);
        // stage1.setMinHeight(700);
        // stage1.setMinWidth(700);
        stage1.sizeToScene();
        stage1.show();
    }

    @FXML
    private void handleMenuAddUser(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("register_view.fxml"));
        Parent root = loader.load();
        Stage stage1 = new Stage();
        stage1.setTitle("Register");
        stage1.initOwner(this.stage);
        stage1.initModality(Modality.APPLICATION_MODAL);
        Scene scene = new Scene(root);
        Helper.loadStyle(scene);
        RegisterController controller = loader.getController();
        controller.initialize(stage1);
        stage1.setScene(scene);
        stage1.setMinHeight(350);
        stage1.setMinWidth(350);
        stage1.sizeToScene();
        stage1.show();
    }
}
