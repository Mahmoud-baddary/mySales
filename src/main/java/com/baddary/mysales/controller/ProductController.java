package com.baddary.mysales.controller;

import java.util.List;
import java.util.Optional;

import com.baddary.mysales.dto.ProductDTO;
import com.baddary.mysales.helper.Helper;
import com.baddary.mysales.mapper.BarcodeMapper;
import com.baddary.mysales.mapper.ProductMapper;
import com.baddary.mysales.row.BarcodeRow;
import com.baddary.mysales.row.ProductRow;
import com.baddary.mysales.service.ProductApiService;

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
import javafx.util.converter.NumberStringConverter;

public class ProductController {
    @FXML
    private TextField tfMUAmount;
    @FXML
    private TextField tfSUAmount;
    @FXML
    private TextField tfSearchByName;
    @FXML
    private TextField tfProductId;
    @FXML
    private TextField tfProductName;
    @FXML
    private TextField tfGreatestUnit;
    @FXML
    private TextField tfMediumUnit;
    @FXML
    private TextField tfSmallestUnit;
    @FXML
    private TableView<BarcodeRow> tblBarcode;
    @FXML
    private TableColumn<BarcodeRow, String> colBarcode;
    @FXML
    private TextField tfBarcode;
    @FXML
    private Button btnUpdate;

    @FXML
    private Button btnAdd;
    @FXML
    private Label lblStatus;
    @FXML
    private TableView<ProductRow> tblProduct;
    @FXML
    private TableColumn<ProductRow, String> colProductName;
    private final ObservableList<ProductRow> productRows = FXCollections.observableArrayList();
    private final ObservableList<BarcodeRow> barcodeRows = FXCollections.observableArrayList();
    private final ProductApiService productApiService = new ProductApiService();
    private Stage stage;

    public void intialize(Stage stage) {
        this.stage = stage;
        // prepare tables
        colBarcode.setCellValueFactory(cellData -> cellData.getValue().barcodeTxtProperty());
        colProductName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        tblProduct.setItems(productRows);
        tblBarcode.setItems(barcodeRows);

        // make update btn enabled when select a product and disable otherwise
        btnUpdate.disableProperty().bind(tblProduct.getSelectionModel().selectedItemProperty().isNull());
        btnAdd.disableProperty().bind(tblProduct.getSelectionModel().selectedItemProperty().isNotNull());
        // load all products into table
        loadAllProducts();
        // on table product selection
        tblProduct.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        Task<Optional<ProductDTO>> byNameAsync = productApiService
                                .findByNameAsync(newValue.getName());
                        Helper.startTask(byNameAsync, e -> {
                            Optional<ProductDTO> optionalDTO = byNameAsync.getValue();
                            optionalDTO.ifPresentOrElse(dto -> {
                                tfProductId.setText(String.valueOf(dto.getId()));
                                tfProductName.setText(dto.getName());
                                tfGreatestUnit.setText(dto.getGreatestUnit());
                                tfMediumUnit.setText(dto.getMediumUnit());
                                tfSmallestUnit.setText(dto.getSmallestUnit());
                                tfMUAmount.setText(String.valueOf(dto.getMediumUnitAmount()));
                                tfSUAmount.setText(String.valueOf(dto.getSmallestUnitAmount()));
                                barcodeRows.clear();
                                barcodeRows.addAll(dto.getBarcodeDTOSet().stream().map(BarcodeMapper::toRow).toList());
                            }, () -> {
                                lblStatus.setText("No product is found with that name");
                            });
                        }, null, this.stage);

                    }
                });

        // on search
        tfSearchByName.textProperty().addListener(
                (observalbe, oldValue, newValue) -> {
                    Timeline debouncer = new Timeline(new KeyFrame(Duration.millis(300),
                            event -> {
                                Task<List<ProductDTO>> searchByNameAsync = productApiService
                                        .searchByNameAsync(newValue);
                                Helper.startTask(searchByNameAsync, e -> {
                                    List<ProductDTO> pList = searchByNameAsync.getValue();
                                    productRows.clear();
                                    productRows.addAll(pList.stream().map(ProductMapper::toRow).toList());
                                }, null, this.stage);

                            }));
                    debouncer.setCycleCount(1);
                    debouncer.play();
                });

        // make su and mu amount accept only numbers
        TextFormatter<Number> mediumAmountFormatter = new TextFormatter<>(
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
        TextFormatter<Number> smallestAmountFormatter = new TextFormatter<>(
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
        tfMUAmount.setTextFormatter(mediumAmountFormatter);
        tfSUAmount.setTextFormatter(smallestAmountFormatter);
    }

    public boolean isRequiredFieldsFilled() {
        return !tfProductName.getText().isBlank() &&
                !tfGreatestUnit.getText().isBlank() &&
                !tfMediumUnit.getText().isBlank() &&
                !tfSmallestUnit.getText().isBlank();
    }

    public void handleUpdateProduct(ActionEvent event) {
        ProductRow productRow = tblProduct.getSelectionModel().getSelectedItem();
        if (productRow != null && isRequiredFieldsFilled()) {
            ProductDTO dto = getProductDTO();
            dto.setId(Long.parseLong(tfProductId.getText()));
            dto.getBarcodeDTOSet().addAll(barcodeRows.stream().map(BarcodeMapper::toDTO).toList());
            Task<ProductDTO> updateProductAsync = productApiService.updateProductAsync(dto.getId(), dto);
            Helper.startTask(updateProductAsync, e -> {
                lblStatus.setText("updated successfully");
                loadAllProducts();
                clear();
            }, null, this.stage);

        } else {
            lblStatus.setText("fill fields");
        }
    }

    public void handleAddProduct(ActionEvent event) {
        if (isRequiredFieldsFilled()) {
            ProductDTO dto = getProductDTO();
            dto.getBarcodeDTOSet().addAll(barcodeRows.stream().map(BarcodeMapper::toDTO).toList());
            Task<ProductDTO> addProductAsync = productApiService.addProductAsync(dto);
            Helper.startTask(addProductAsync, e -> {
                lblStatus.setText("added successfully");
                loadAllProducts();
                clear();
            }, null, this.stage);

        } else {
            lblStatus.setText("fill required fields");
        }
    }

    private ProductDTO getProductDTO() {
        String productName = tfProductName.getText();
        String greatestUnit = tfGreatestUnit.getText();
        String mediumUnit = tfMediumUnit.getText();
        String smallestUnit = tfSmallestUnit.getText();
        int mUAmount = Integer.parseInt(tfMUAmount.getText());
        int sUAmount = Integer.parseInt(tfSUAmount.getText());
        ProductDTO dto = new ProductDTO();
        dto.setName(productName);
        dto.setGreatestUnit(greatestUnit);
        dto.setMediumUnit(mediumUnit);
        dto.setSmallestUnit(smallestUnit);
        dto.setMediumUnitAmount(mUAmount);
        dto.setSmallestUnitAmount(sUAmount);
        return dto;
    }

    public void handleRemoveBarcode(ActionEvent event) {
        BarcodeRow row = tblBarcode.getSelectionModel().getSelectedItem();
        if (row != null) {
            barcodeRows.remove(row);
        }
    }

    public void handleNewProduct(ActionEvent event) {
        clear();
    }

    public void clear() {
        tfBarcode.clear();
        tfProductName.clear();
        tfGreatestUnit.clear();
        tfMediumUnit.clear();
        tfSmallestUnit.clear();
        tfProductId.clear();
        barcodeRows.clear();
        tfMUAmount.clear();
        tfSUAmount.clear();
    }

    public void handleAddBarcode(ActionEvent event) {
        String barcode;
        if (!(barcode = tfBarcode.getText()).isBlank()) {
            BarcodeRow row = new BarcodeRow();
            row.setBarcodeTxt(barcode);
            barcodeRows.add(row);
        }
    }

    private void loadAllProducts() {
        Task<List<ProductDTO>> allAsync = productApiService.findAllAsync();
        Helper.startTask(allAsync, e -> {
            List<ProductDTO> pList = allAsync.getValue();
            productRows.clear();
            productRows.addAll(pList.stream().map(ProductMapper::toRow).toList());
        }, null, this.stage);
    }

}
