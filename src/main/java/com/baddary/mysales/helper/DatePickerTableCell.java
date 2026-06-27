package com.baddary.mysales.helper;

import javafx.scene.control.ContentDisplay;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;

import java.time.LocalDate;

import com.baddary.mysales.row.OrderProductPurchaseRow;

public class DatePickerTableCell<S> extends TableCell<S, LocalDate> {

    private final DatePicker datePicker = new DatePicker();

    public DatePickerTableCell() {

        datePicker.setEditable(false);

        datePicker.setDayCellFactory(dp -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setDisable(empty || item.isBefore(LocalDate.now()));
            }
        });

        datePicker.setOnAction(e ->
                commitEdit(datePicker.getValue())
        );

        setContentDisplay(ContentDisplay.TEXT_ONLY);
    }

    @Override
    public void startEdit() {
        super.startEdit();
        if (!isEmpty()) {
            datePicker.setValue(getItem());
            setGraphic(datePicker);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            datePicker.requestFocus();
            datePicker.show();
        }
    }

    @Override
    public void commitEdit(LocalDate newValue) {
        super.commitEdit(newValue);
        ((OrderProductPurchaseRow) getTableRow().getItem()).setExpireDate(newValue);
        setContentDisplay(ContentDisplay.TEXT_ONLY);
    }

    @Override
    protected void updateItem(LocalDate item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setText(null);
            setGraphic(null);
            return;
        }

        setGraphic(datePicker);

        if (isEditing()) {
            datePicker.setValue(item);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        } else {
            setText(item == null ? "" : item.toString());
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        }
    }
}
