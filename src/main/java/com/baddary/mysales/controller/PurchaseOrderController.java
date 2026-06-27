package com.baddary.mysales.controller;

import com.baddary.mysales.MainApplication;
import com.baddary.mysales.dto.CustomerDTO;
import com.baddary.mysales.dto.OrderDTO;
import com.baddary.mysales.dto.OrderProductDTO;
import com.baddary.mysales.dto.ProductDTO;
import com.baddary.mysales.enums.OrderType;
import com.baddary.mysales.enums.PaymentType;
import com.baddary.mysales.helper.Helper;
import com.baddary.mysales.mapper.OrderProductMapper;
import com.baddary.mysales.row.OrderProductPurchaseRow;
import com.baddary.mysales.service.CustomerApiService;
import com.baddary.mysales.service.OrderApiService;
import com.baddary.mysales.service.ProductApiService;
import com.baddary.mysales.util.TokenStore;
import com.baddary.mysales.helper.DatePickerTableCell;

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.*;
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
import javafx.scene.control.ToggleGroup;

public class PurchaseOrderController {
    @FXML
    private TableColumn<OrderProductPurchaseRow, String> colNote;
    @FXML
    private RadioButton radioBtnDeffered;
    @FXML
    private RadioButton radioBtnInstant;
    @FXML
    private TableColumn<OrderProductPurchaseRow, Number> colDiscount;
    @FXML
    private TableColumn<OrderProductPurchaseRow, String> colBatch;
    @FXML
    private TableColumn<OrderProductPurchaseRow, LocalDate> colExpire;
    @FXML
    private TextField tfCustomerName;
    @FXML
    private TextField tfPhoneNumber;
    @FXML
    private TextArea taAddress;
    @FXML
    private TableView<OrderProductPurchaseRow> tblProducts;
    @FXML
    private TableColumn<OrderProductPurchaseRow, Number> colProductId;
    @FXML
    private TableColumn<OrderProductPurchaseRow, Number> colQuantity;
    @FXML
    private TableColumn<OrderProductPurchaseRow, String> colProductName;
    @FXML
    private TableColumn<OrderProductPurchaseRow, Number> colPrice;
    @FXML
    private TableColumn<OrderProductPurchaseRow, Number> colTotal;
    @FXML
    private Label lblTotalPrice;
    @FXML
    private TextField tfDiscount;
    @FXML
    private Label lblFinalPrice;
    @FXML
    private Label lblStatus;
    @FXML
    private TableColumn<OrderProductPurchaseRow, String> colUnit;
    @FXML
    private TextField tfEmail;
    @FXML
    private TextField tfCustomerId;
    private final ObservableList<String> units = FXCollections.observableArrayList();
    private final ObservableList<OrderProductPurchaseRow> purchaseRows = FXCollections.observableArrayList(
            op -> new Observable[] {
                    op.totalPriceProperty()
            });
    private final CustomerApiService customerApiService = new CustomerApiService();
    private final OrderApiService orderApiService = new OrderApiService();
    private final ProductApiService productApiService = new ProductApiService();
    private Stage stage;
    @FXML
    private ToggleGroup paymentGroup;

    public void initialize(Stage stage) {
        this.stage = stage;
        // initialize table
        prepareTableCells();
        setupFocusSeries();
        tblProducts.setItems(purchaseRows);

        // most purchase orders are differed so
        radioBtnDeffered.setSelected(true);

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
            }, null, this.stage);

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
        purchaseRows.addListener((ListChangeListener<OrderProductPurchaseRow>) c -> {
            while (c.next()) {

                if (c.wasAdded()) {

                    List<OrderProductPurchaseRow> duplicates = new ArrayList<>();

                    for (OrderProductPurchaseRow op : c.getAddedSubList()) {
                        if (isProductExist(op)) {
                            duplicates.add(op);
                        }
                        op.expireDateProperty().addListener((obs, old, val) -> {
                            if (isProductExist(op)) {
                                Helper.createAlertError(
                                        "Error",
                                        "This product already exists with the same expire date").show();

                                // 🔥 MODIFY OUTSIDE CHANGE ITERATION
                                Platform.runLater(() -> op.setExpireDate(old));
                            }
                        });
                    }

                    if (!duplicates.isEmpty()) {
                        Helper.createAlertError(
                                "Error",
                                "This product was already added with the same expire date").show();

                        // 🔥 MODIFY OUTSIDE CHANGE ITERATION
                        Platform.runLater(() -> purchaseRows.removeAll(duplicates));
                    }

                    // UI selection (safe)
                    Platform.runLater(() -> {
                        int row = purchaseRows.size() - 1;
                        if (row >= 0) {
                            tblProducts.scrollTo(row);
                            tblProducts.getSelectionModel().select(row);
                        }
                    });
                }
            }
        });

        // bind total and final price
        DoubleBinding totalPriceBinding = Bindings.createDoubleBinding(
                () -> purchaseRows.stream().mapToDouble(OrderProductPurchaseRow::getTotalPrice).sum(), purchaseRows);

        lblTotalPrice.textProperty().bind(totalPriceBinding.asString("%.2f"));

        DoubleProperty discountProperty = new SimpleDoubleProperty(0);
        tfDiscount.textProperty().bindBidirectional(discountProperty, new NumberStringConverter());
        DoubleBinding finalPriceBinding = totalPriceBinding.multiply(
                discountProperty.divide(100).negate().add(1));

        lblFinalPrice.textProperty().bind(
                finalPriceBinding.asString("%.2f"));

    }

    public void handleAddProduct(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("purchase_add_product_view.fxml"));
        Parent root = loader.load();
        Stage stage1 = new Stage();
        PurchaseAddProductController controller = loader.getController();
        controller.initialize(stage1, purchaseRows);
        stage1.initOwner(this.stage);
        stage1.initModality(Modality.APPLICATION_MODAL);
        stage1.setTitle("Search for product");
        stage1.setMinHeight(700);
        stage1.setMinWidth(600);
        stage1.sizeToScene();
        stage1.setScene(new Scene(root, 400, 300));
        stage1.show();
    }

    public void handleRemoveProduct(ActionEvent event) {
        OrderProductPurchaseRow op = tblProducts.getSelectionModel().getSelectedItem();
        if (op != null) {
            purchaseRows.remove(op);
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
        if (purchaseRows.isEmpty()) {
            lblStatus.setText("No products were added");
            return;
        }
        PaymentType paymentType = radioBtnInstant.isSelected() ? PaymentType.INSTANT : PaymentType.DEFERRED;
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.now();
        double discount = Double.parseDouble(tfDiscount.getText());

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setCustomerId(customerId);
        orderDTO.setUserId(TokenStore.getCurrentUserId());
        orderDTO.setDate(date);
        orderDTO.setTime(time);
        orderDTO.setDiscount(discount);
        orderDTO.setPaymentType(paymentType);
        orderDTO.setOrderType(OrderType.BUY);
        List<OrderProductDTO> orderProductDTOS = purchaseRows.stream().map(OrderProductMapper::toDTO).toList();
        orderDTO.getOrderProductDTOSet().addAll(orderProductDTOS);
        Helper.startTask(orderApiService.addOrderAsync(orderDTO), e -> {
            purchaseRows.clear();
            lblStatus.setText("saved");
        }, null, this.stage);

    }

    public void prepareTableCells() {
        colProductId.setCellValueFactory(
                cellData -> cellData.getValue().productIdProperty());

        colUnit.setCellFactory(ChoiceBoxTableCell.forTableColumn(units));
        colUnit.setCellValueFactory(cellData -> cellData.getValue().unitProperty());
        // prepare units list
        colUnit.setOnEditStart(event -> {
            units.clear();
            Task<Optional<ProductDTO>> byIdAsync = productApiService.findByIdAsync(event.getRowValue().getProductId());
            ;
            Helper.startTask(byIdAsync, e -> {
                byIdAsync.getValue().ifPresent(productDTO -> {
                    units.add(productDTO.getGreatestUnit());
                    units.add(productDTO.getMediumUnit());
                    units.add(productDTO.getSmallestUnit());
                });
            }, null, this.stage);

        });
        colUnit.setOnEditCommit(event -> tblProducts.requestFocus());

        colProductName.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getProductDTO().getName()));

        colPrice.setCellValueFactory(cellData -> cellData.getValue().priceProperty());
        colPrice.setCellFactory(TextFieldTableCell.forTableColumn(new NumberStringConverter()));
        colPrice.setOnEditCommit(event -> {
            double newValue = event.getNewValue().doubleValue();
            double oldValue = event.getOldValue().doubleValue();
            OrderProductPurchaseRow row = event.getRowValue();
            if (newValue > 0) {
                row.setPrice(newValue);
            } else {
                row.setPrice(oldValue);
            }
            tblProducts.refresh();
            tblProducts.requestFocus();
        });

        colExpire.setEditable(true);
        tblProducts.setEditable(true);
        colExpire.setCellValueFactory(cellData -> cellData.getValue().expireDateProperty());
        colExpire.setCellFactory(col -> new DatePickerTableCell<>());
        colExpire.setOnEditCommit(event -> tblProducts.requestFocus());
        colTotal.setCellValueFactory(cellData -> cellData.getValue().totalPriceProperty());
        colQuantity.setCellValueFactory(cellData -> cellData.getValue().quantityProperty());
        colQuantity.setEditable(true);
        colQuantity.setCellFactory(TextFieldTableCell.forTableColumn(new NumberStringConverter()));
        colQuantity.setOnEditCommit(event -> {
            // quantity must be positive
            double value = event.getNewValue().doubleValue();
            OrderProductPurchaseRow row = event.getRowValue();
            if (value > 0) {
                row.setQuantity(value);
            } else {
                row.setQuantity(event.getOldValue().doubleValue());
            }
            tblProducts.requestFocus();
        });

        colDiscount.setCellValueFactory(cellData -> cellData.getValue().discountProperty());
        colDiscount.setEditable(true);
        colDiscount.setCellFactory(TextFieldTableCell.forTableColumn(new NumberStringConverter()));
        colDiscount.setOnEditCommit(event -> {
            tblProducts.requestFocus();
        });

        colBatch.setCellFactory(TextFieldTableCell.forTableColumn());
        colBatch.setCellValueFactory(cellData -> cellData.getValue().batchProperty());
        colBatch.setOnEditCommit(event -> tblProducts.requestFocus());
        colNote.setCellFactory(TextFieldTableCell.forTableColumn());
        colNote.setCellValueFactory(cellData -> cellData.getValue().noteProperty());
        colNote.setOnEditCommit(event -> tblProducts.requestFocus());

    }

    public void handleMenuManageCustomers(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("customer_view.fxml"));
        Parent root = loader.load();
        Stage stage1 = new Stage();
        stage1.initOwner(this.stage);
        stage1.initModality(Modality.APPLICATION_MODAL);
        CustomerController controller = loader.getController();
        controller.intialize(stage1);
        stage1.setTitle("Add Customer");
        stage1.setScene(new Scene(root));
        stage1.setMinHeight(600);
        stage1.setMinWidth(900);
        stage1.sizeToScene();

        stage1.show();
        // customerController.openInFullScreen();
    }

    public void handleMenuManageProducts(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("product_view.fxml"));
        Parent root = loader.load();
        Stage stage1 = new Stage();
        stage1.initOwner(this.stage);
        stage1.initModality(Modality.APPLICATION_MODAL);
        ProductController controller = loader.getController();
        controller.intialize(stage1);
        stage1.setTitle("Manage Products");
        stage1.setScene(new Scene(root));
        stage1.setMinHeight(600);
        stage1.setMinWidth(900);
        stage1.sizeToScene();
        stage1.show();

    }

    public void handleMenuSalesOrder(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("sales_order_view.fxml"));
        Parent root = loader.load();
        SalesOrderController controller = loader.getController();
        controller.initialize(this.stage);
        this.stage.setTitle("Sales Order");
        this.stage.setScene(new Scene(root));
        this.stage.sizeToScene();
    }

    private boolean isProductExist(OrderProductPurchaseRow row) {
        return this.purchaseRows.stream()
                .filter(op -> Objects.equals(op.getProductId(), row.getProductId()))
                .filter(op -> op.getExpireDate().equals(row.getExpireDate()))
                .count() > 1;
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

    @FXML
    private void handleMenuMyStock(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("stock_view.fxml"));
        Parent root = loader.load();
        Stage stage1 = new Stage();
        stage1.setTitle("Stock");
        stage1.initOwner(this.stage);
        stage1.initModality(Modality.APPLICATION_MODAL);
        Scene scene = new Scene(root);
        StockController controller = loader.getController();
        controller.initialize(stage1);
        Helper.loadStyle(scene);
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
        OrderSearchController controller = loader.getController();
        controller.intialize(stage1);
        Helper.loadStyle(scene);
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
        RegisterController controller = loader.getController();
        controller.initialize(stage1);
        Helper.loadStyle(scene);
        stage1.setScene(scene);
        stage1.setMinHeight(350);
        stage1.setMinWidth(350);
        stage1.sizeToScene();
        stage1.show();
    }
}
