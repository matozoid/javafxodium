package com.laamella.javafxodium;

import io.vavr.control.Option;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import nz.sodium.Cell;
import nz.sodium.CellSink;

public class FxComboBox<E> extends ComboBox<E> {
    public FxComboBox() {
        selectedItem = mkSelectedItem();
    }

    public FxComboBox(ObservableList<E> items) {
        super(items);
        selectedItem = mkSelectedItem();
    }

    private Cell<Option<E>> mkSelectedItem() {
        E sel = getValue();
        CellSink<Option<E>> selectedItem = new CellSink<>(
                sel == null ? Option.none() : Option.some(sel));

        valueProperty().addListener((observable, oldValue, newValue) -> selectedItem.send(Option.some(newValue)));
        return selectedItem;
    }

    public final Cell<Option<E>> selectedItem;
}

