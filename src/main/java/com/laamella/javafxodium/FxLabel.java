package com.laamella.javafxodium;

import javafx.application.Platform;
import javafx.scene.control.Label;
import nz.sodium.Cell;
import nz.sodium.Operational;
import nz.sodium.Transaction;

import static com.laamella.javafxodium.ListenerManager.addManagedListener;

public class FxLabel extends Label {
    public FxLabel(Cell<String> text) {
        super("");
        addManagedListener(this, () ->
                Operational.updates(text).listen(t -> Util.runInFX(() -> setText(t))));
        // Set the text at the end of the transaction so SLabel works with CellLoops.
        Transaction.post(() -> Platform.runLater(() -> setText(text.sample())));
    }
}
