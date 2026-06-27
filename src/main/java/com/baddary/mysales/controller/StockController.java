package com.baddary.mysales.controller;

import java.util.List;
import java.util.Optional;

import com.baddary.mysales.dto.ProductDTO;
import com.baddary.mysales.dto.StockDTO;
import com.baddary.mysales.helper.Helper;
import com.baddary.mysales.row.StockRow;
import com.baddary.mysales.service.ProductApiService;
import com.baddary.mysales.service.StockApiService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.stage.Stage;

public class StockController {
        @FXML
        private TextField tfSearch;
        @FXML
        private TableView<StockRow> stockTable;
        @FXML
        private TableColumn<StockRow, String> nameColumn;
        @FXML
        private TableColumn<StockRow, Number> quantityColumn;
        @FXML
        private TableColumn<StockRow, String> unitColumn;
        @FXML
        private TableColumn<StockRow, String> expireColumn;
        @FXML
        private TableColumn<StockRow, Number> priceColumn;

        private final ObservableList<String> units = FXCollections.observableArrayList();
        private final ObservableList<StockRow> stockRows = FXCollections.observableArrayList();
        private final StockApiService stockApiService = new StockApiService();
        private final ProductApiService productApiService = new ProductApiService();
        private Stage stage;
        @FXML
        public void initialize(Stage stage) {
                this.stage = stage;
                // bind columns
                stockTable.setItems(stockRows);
                stockTable.setEditable(true);
                nameColumn.setCellValueFactory(cellData -> cellData.getValue().productNameProperty());
                quantityColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty());
                unitColumn.setCellFactory(ChoiceBoxTableCell.forTableColumn(units));
                unitColumn.setCellValueFactory(cellData -> cellData.getValue().unitProperty());
                unitColumn.setOnEditStart(event -> {
                        units.clear();
                        ProductDTO productDTO = event.getRowValue().getProductDTO();
                        units.add(productDTO.getGreatestUnit());
                        units.add(productDTO.getMediumUnit());
                        units.add(productDTO.getSmallestUnit());
                });
                expireColumn.setCellValueFactory(cellData -> cellData.getValue().expireProperty());
                priceColumn.setCellValueFactory(cellData -> cellData.getValue().priceProperty());
        }

        public void refreshStock(ActionEvent event) {
                String productName = tfSearch.getText();
                Task<List<StockDTO>> stocksAsync = stockApiService.findStocksAsync(productName);
                Helper.startTask(stocksAsync, e -> {
                        stocksAsync.getValue().forEach(stockDTO -> {
                                Task<Optional<ProductDTO>> byIdAsync = productApiService
                                                .findByIdAsync(stockDTO.getProductId());
                                Helper.startTask(byIdAsync, ev -> {
                                        byIdAsync.getValue().ifPresent(productDTO -> {
                                                StockRow row = new StockRow();
                                                row.setExpire(stockDTO.getExpire().toString());
                                                row.setPriceSU(stockDTO.getPriceSU());
                                                row.setUnit(productDTO.getGreatestUnit());
                                                row.setProductDTO(productDTO);
                                                row.setProductName(productDTO.getName());
                                                row.setQuantitySU(stockDTO.getQuantitySU());
                                                stockRows.add(row);
                                        });

                                }, null, this.stage);
                        });
                }, null, this.stage);
        }

        public void closeWindow(ActionEvent event) {
        }
}
