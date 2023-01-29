package com.laamella.javafxodium;

import javafx.application.Platform;
import javafx.scene.control.Button;
import nz.sodium.*;

import static com.laamella.javafxodium.ListenerManager.addManagedListener;
import static nz.sodium.Unit.UNIT;

public class FxButton extends Button {
    public final Stream<Unit> sClicked;

    public FxButton(String label) {
        this(label, new Cell<>(true));
    }

    public FxButton(String label, Cell<Boolean> enabled) {
        super(label);
        StreamSink<Unit> sClickedSink = new StreamSink<>();
        this.sClicked = sClickedSink;
        setOnAction(actionEvent -> sClickedSink.send(UNIT));
        // Do it at the end of the transaction so it works with looped cells
        Transaction.post(() -> setDisable(!enabled.sample()));

        addManagedListener(this, () -> Operational.updates(enabled)
                .listen(ena -> Util.runInFX(() -> FxButton.this.setDisable(!ena))));
    }
}
