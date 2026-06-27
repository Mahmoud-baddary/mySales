package com.baddary.mysales.controller;

import com.baddary.mysales.dto.ProductDTO;
import com.baddary.mysales.helper.Helper;
import com.baddary.mysales.mapper.ProductMapper;
import com.baddary.mysales.row.OrderProductPurchaseRow;
import com.baddary.mysales.row.ProductRow;
import com.baddary.mysales.service.ProductApiService;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.converter.NumberStringConverter;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class PurchaseAddProductController {
    @FXML
    private DatePicker dpExpireDate;
    @FXML
    private ComboBox<String> cbUnit;
    @FXML
    private TextField tfQuantity;
    @FXML
    private TextField tfPrice;
    @FXML
    private TextField tfDiscount;
    @FXML
    private TextField tfBatch;
    @FXML
    private TextField tfProductName;
    @FXML
    private Button btnClear;
    @FXML
    private ListView<ProductRow> listSearchResult;
    @FXML
    private Button btnSelect;
    @FXML
    private Button btnCancel;
    private ObservableList<OrderProductPurchaseRow> orderProductsObs;
    private final ObservableList<String> units = FXCollections.observableArrayList();
    private final OrderProductPurchaseRow opdto = new OrderProductPurchaseRow();
    private final ProductApiService productApiService = new ProductApiService();
    private Stage stage;
    @FXML
    private Label lblStatus;

    public void initialize(Stage stage, ObservableList<OrderProductPurchaseRow> orderProductsObs) {
        this.stage = stage;
        this.orderProductsObs = orderProductsObs;
        cbUnit.setItems(units);
        dpExpireDate.setEditable(false);

        dpExpireDate.focusedProperty().addListener((obs, old, focused) -> {
            if (focused)
                Platform.runLater(dpExpireDate::show);
        });
        // disable expire date before now
        dpExpireDate.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item.isBefore(LocalDate.now())) {
                    setDisable(true);
                }
            }
        });
        // binding
        cbUnit.valueProperty().bindBidirectional(opdto.unitProperty());
        dpExpireDate.valueProperty().bindBidirectional(opdto.expireDateProperty());
        tfBatch.textProperty().bindBidirectional(opdto.batchProperty());
        setupFocusChain();

        btnSelect.disableProperty().bind(listSearchResult.getSelectionModel().selectedItemProperty().isNull());
        tfProductName.textProperty().addListener((observable, oldValue, newValue) -> {
            Timeline debouncer = new Timeline(new KeyFrame(Duration.millis(300), event -> {
                if (newValue.isBlank()) {
                    return;
                }
                Task<List<ProductDTO>> searchByNameAsync = productApiService.searchByNameAsync(newValue);
                Helper.startTask(searchByNameAsync,
                        e -> {
                            List<ProductDTO> productDTOList = searchByNameAsync.getValue();
                            listSearchResult.getItems().clear();
                            listSearchResult.getItems()
                                    .addAll(productDTOList.stream().map(ProductMapper::toRow).toList());
                        }, null, this.stage);

            }));
            debouncer.setCycleCount(1);
            debouncer.play();
        });

        // make price, quantity and price accept only double
        TextFormatter<Number> quantityFormatter = new TextFormatter<>(
                new NumberStringConverter(), 0.0, change -> {
                    String text = change.getControlNewText();
                    if (text.isEmpty())
                        return change;
                    try {
                        double val = Double.parseDouble(text);
                        if (val > 0)
                            return change;
                        else
                            return null;
                    } catch (Exception e) {
                        return null;
                    }
                });
        TextFormatter<Number> priceFormatter = new TextFormatter<>(
                new NumberStringConverter(), 0.0, change -> {
                    String text = change.getControlNewText();
                    if (text.isEmpty())
                        return change;
                    try {
                        double val = Double.parseDouble(text);
                        if (val > 0)
                            return change;
                        else
                            return null;
                    } catch (Exception e) {
                        return null;
                    }
                });
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
        tfQuantity.setTextFormatter(quantityFormatter);
        tfPrice.setTextFormatter(priceFormatter);
        tfDiscount.setTextFormatter(discountFormatter);
        opdto.quantityProperty().bindBidirectional(quantityFormatter.valueProperty());
        opdto.priceProperty().bindBidirectional(priceFormatter.valueProperty());
        opdto.discountProperty().bindBidirectional(discountFormatter.valueProperty());
        // listener of listview
        listSearchResult.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        Task<Optional<ProductDTO>> byIdAsync = productApiService.findByIdAsync(newValue.getId());
                        Helper.startTask(byIdAsync, ev -> {
                            byIdAsync.getValue().ifPresent(productDTO -> {
                                units.clear();
                                units.add(productDTO.getGreatestUnit());
                                units.add(productDTO.getMediumUnit());
                                units.add(productDTO.getSmallestUnit());
                                cbUnit.getSelectionModel().selectFirst();
                                opdto.setProductDTO(productDTO);
                                opdto.setProductId(productDTO.getId());
                            });
                        }, null, this.stage);

                    }
                });
    }

    

    public void handleClear(ActionEvent actionEvent) {
        tfProductName.clear();
    }

    public void handleItemSelection(MouseEvent mouseEvent) {
    }

    public void handleKeySelection(KeyEvent keyEvent) {
    }

    public void handleSelect(ActionEvent actionEvent) {
        ProductRow productRow = listSearchResult.getSelectionModel().getSelectedItem();
        if (productRow != null && isDataFilled() && isDataAccepted()) {
            OrderProductPurchaseRow copy = new OrderProductPurchaseRow();
            copy.setExpireDate(opdto.getExpireDate());
            copy.setBatch(opdto.getBatch());
            copy.setProductDTO(opdto.getProductDTO());
            copy.setProductId(opdto.getProductId());
            copy.setNote(opdto.getNote());
            copy.setPrice(opdto.getPrice());
            copy.setQuantity(opdto.getQuantity());
            copy.setDiscount(opdto.getDiscount());
            copy.setUnit(opdto.getUnit());
            orderProductsObs.add(copy);
            clear();
        } else {
            lblStatus.setText("More data is needed");
            Helper.createAlertError("More Data is needed", "You should choose a product and fill all the fields")
                    .show();
        }
    }

    public void handleCancel(ActionEvent actionEvent) {
        Node node = (Node) actionEvent.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        stage.close();
    }

    private boolean isDataFilled() {
        return !(tfBatch.getText().isBlank() ||
                tfDiscount.getText().isBlank() ||
                tfPrice.getText().isBlank() ||
                tfQuantity.getText().isBlank() ||
                dpExpireDate.valueProperty().isNull().get());
    }

    private boolean isDataAccepted() {
        return opdto.getQuantity() > 0 || opdto.getPrice() > 0;
    }

   

    private void setupFocusChain() {
        tfProductName.setOnAction(e -> {
            listSearchResult.requestFocus();
            listSearchResult.getSelectionModel().selectFirst();
        });
    }

    private void clear() {
        tfProductName.setText("");
        listSearchResult.getItems().clear();
        opdto.setPrice(0);
        opdto.setBatch("");
        opdto.setProductId(0);
        opdto.setDiscount(0);
        opdto.setNote("");
        opdto.setUnit("");
        opdto.setExpireDate(null);
        opdto.setQuantity(0);

    }

}
