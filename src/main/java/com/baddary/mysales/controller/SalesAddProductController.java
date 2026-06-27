package com.baddary.mysales.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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

import com.baddary.mysales.dto.ProductDTO;
import com.baddary.mysales.dto.StockDTO;
import com.baddary.mysales.helper.Helper;
import com.baddary.mysales.mapper.ProductMapper;
import com.baddary.mysales.row.OrderProductSaleRow;
import com.baddary.mysales.row.ProductRow;
import com.baddary.mysales.service.ProductApiService;
import com.baddary.mysales.service.StockApiService;

public class SalesAddProductController {
    @FXML
    private TextField tfProductName;
    @FXML
    private Button btnClear;
    @FXML
    private ListView<ProductRow> listSearchResult;
    @FXML
    private ComboBox<String> cbUnit;
    @FXML
    private TextField tfQuantity;
    @FXML
    private TextField tfPrice;
    @FXML
    private ComboBox<LocalDate> cbExpireDate;
    @FXML
    private TextField tfBatch;
    @FXML
    private Button btnSelect;
    @FXML
    private Button btnCancel;
    @FXML
    private TextField tfStock;
    private final ObservableList<String> units = FXCollections.observableArrayList();
    private final ObservableList<LocalDate> expires = FXCollections.observableArrayList();
    private ObservableList<OrderProductSaleRow> orderProductsObs;
    private final OrderProductSaleRow saleRow = new OrderProductSaleRow();
    private final ProductApiService productApiService = new ProductApiService();
    private final StockApiService stockApiService = new StockApiService();
    private Stage stage;
   
    @FXML
    private Label lblStatus;

    
   

    public void initialize(Stage stage, ObservableList<OrderProductSaleRow> orderProductsObs) {
        this.stage = stage;
        this.orderProductsObs = orderProductsObs;
        cbUnit.setItems(units);
        cbExpireDate.setItems(expires);
        tfQuantity.textProperty().bindBidirectional(
                saleRow.quantityProperty(), new NumberStringConverter());
        tfStock.textProperty().bindBidirectional(saleRow.stockProperty(), new NumberStringConverter());
        tfPrice.textProperty().bindBidirectional(saleRow.priceProperty(), new NumberStringConverter());
        tfBatch.textProperty().bindBidirectional(saleRow.batchProperty());
        cbUnit.valueProperty().bindBidirectional(saleRow.unitProperty());
        cbExpireDate.valueProperty().bindBidirectional(saleRow.expireDateProperty());
        setupFocusChain();
        btnSelect.disableProperty().bind(saleRow.productIdProperty().lessThanOrEqualTo(0));

        // listener for tf productname to add products in the list
        tfProductName.textProperty().addListener((observable, oldValue, newValue) -> {
            Timeline debouncer = new Timeline(new KeyFrame(Duration.millis(300), event -> {
                if (newValue.isBlank()) {
                    return;
                }
                Task<List<ProductDTO>> searchByNameAsync = productApiService.searchByNameAsync(newValue);
                ;
                Helper.startTask(searchByNameAsync, e -> {
                    List<ProductDTO> productDTOs = searchByNameAsync.getValue();
                    listSearchResult.getItems().clear();
                    listSearchResult.getItems().addAll(productDTOs.stream().map(ProductMapper::toRow).toList());
                }, null, this.stage);

            }));
            debouncer.setCycleCount(1);
            debouncer.play();
        });

        // make sure that quantity will not be bigger than stock
        cbUnit.getSelectionModel().selectedItemProperty().addListener((observableValue, string, newVal) -> {
            saleRow.setUnit(newVal);
            if (saleRow.getQuantity() > saleRow.getStock()) {
                saleRow.setQuantity(saleRow.getStock());
            }
        });

        TextFormatter<Number> quantityFormatter = new TextFormatter<>(
                new NumberStringConverter(), 0.0, change -> {
                    String text = change.getControlNewText();
                    if (text.isEmpty())
                        return change;
                    try {
                        double val = Double.parseDouble(text);
                        if (saleRow.getStock() < val) {
                            return null;
                        }
                        return change;
                    } catch (Exception e) {
                        return null;
                    }
                });

        tfQuantity.setTextFormatter(quantityFormatter);
        // listener for listview
        listSearchResult.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        Task<Optional<ProductDTO>> byIdAsync = productApiService.findByIdAsync(newValue.getId());
                        Helper.startTask(byIdAsync, e -> {
                            Optional<ProductDTO> optionalDto = byIdAsync.getValue();
                            optionalDto.ifPresent(dto -> {
                                units.clear();
                                units.add(dto.getGreatestUnit());
                                units.add(dto.getMediumUnit());
                                units.add(dto.getSmallestUnit());
                                cbUnit.getSelectionModel().selectFirst();
                                expires.clear();
                                Task<List<StockDTO>> stocksAsync = stockApiService.findStocksAsync(dto.getId());
                                Helper.startTask(stocksAsync, ev -> {
                                    List<StockDTO> stockDTOs = stocksAsync.getValue();
                                    if (!stockDTOs.isEmpty()) {
                                        stockDTOs.forEach(stock -> {
                                            expires.add(stock.getExpire());
                                        });
                                        cbExpireDate.getSelectionModel().selectFirst();
                                        saleRow.setProductId(dto.getId());
                                        saleRow.setProductDTO(dto);
                                        saleRow.setUnit(dto.getGreatestUnit());
                                        if (saleRow.getStock() > 1) {
                                            saleRow.setQuantity(1);
                                        } else {
                                            saleRow.setQuantity(saleRow.getStock());
                                        }
                                    } else {
                                        Helper.createAlertInfo("Empty stock", "There is no stock of this product")
                                                .show();
                                        tfProductName.requestFocus();
                                        clear();
                                    }

                                }, null, this.stage);
                            });
                        }, null, this.stage);

                    }
                });

        // update values of batch, stock, price when expire date change
        cbExpireDate.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            ProductRow productRow = listSearchResult.getSelectionModel().getSelectedItem();
            if (productRow == null)
                return;
            Task<Optional<ProductDTO>> byIdAsync = productApiService.findByIdAsync(productRow.getId());
            Helper.startTask(byIdAsync, e -> {
                Optional<ProductDTO> optionalDTO = byIdAsync.getValue();
                optionalDTO.ifPresent(dto -> {
                    Task<Optional<StockDTO>> stockByProductAndExpireAsync = stockApiService
                            .findStockByProductAndExpireAsync(dto.getId(), newValue);
                    Helper.startTask(stockByProductAndExpireAsync, ev -> {
                        Optional<StockDTO> optionalStockDTO = stockByProductAndExpireAsync.getValue();
                        optionalStockDTO.ifPresent(stock -> {
                            saleRow.setBatch(stock.getBatch());
                            saleRow.setStockSU(stock.getQuantitySU());
                            saleRow.setPriceSU(stock.getPriceSU());
                            // quantity must not be bigger thant stock
                            if (saleRow.getQuantity() > saleRow.getStock()) {
                                saleRow.setQuantity(saleRow.getStock());
                            }
                        });
                    }, null, this.stage);

                });
            }, null, this.stage);

        });
    }

    private void setupFocusChain() {
        tfProductName.setOnAction(e -> {
            listSearchResult.requestFocus();
            listSearchResult.getSelectionModel().selectFirst();
        });
    }

    public void handleClear(ActionEvent event) {
        listSearchResult.getItems().clear();
        clear();
    }

    public void handleKeySelection(KeyEvent keyEvent) {
    }

    public void handleItemSelection(MouseEvent mouseEvent) {
    }

    public void handleSelect(ActionEvent event) {
        ProductRow productRow = listSearchResult.getSelectionModel().getSelectedItem();
        if (productRow != null && isDataFilled()) {
            OrderProductSaleRow copy = new OrderProductSaleRow();
            copy.setUnit(saleRow.getUnit());
            copy.setBatch(saleRow.getBatch());
            copy.setProductId(saleRow.getProductId());
            copy.setProductDTO(saleRow.getProductDTO());
            copy.setQuantity(saleRow.getQuantity());
            copy.setStockSU(saleRow.getStockSU());
            copy.setPriceSU(saleRow.getPriceSU());
            copy.setExpireDate(saleRow.getExpireDate());
            orderProductsObs.add(copy);
            clear();
        }else{
            lblStatus.setText("fill all data required");
        }

    }

    public void handleCancel(ActionEvent event) {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        stage.close();
    }

    private void clear() {
        listSearchResult.getItems().clear();
        saleRow.setBatch("");
        saleRow.setProductDTO(null);
        saleRow.setProductId(0);
        saleRow.setUnit("");
        saleRow.setExpireDate(null);
        saleRow.setQuantity(0);
        units.clear();
        expires.clear();
        tfProductName.setText("");
    }

    private boolean isDataFilled() {
        return !(saleRow.getBatch().isBlank() ||
                saleRow.getUnit().isBlank() ||
                saleRow.getQuantity() <= 0 ||
                saleRow.expireDateProperty().isNull().get());
    }
}
