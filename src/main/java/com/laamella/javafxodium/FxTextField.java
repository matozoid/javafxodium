package com.laamella.javafxodium;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;
import nz.sodium.*;

import static com.laamella.javafxodium.ListenerManager.addManagedListener;

public class FxTextField extends TextField {
    public FxTextField(String initText) {
        this(new Stream<>(), initText, 15);
    }

    public FxTextField(String initText, int width) {
        this(new Stream<>(), initText, width);
    }

    public FxTextField(Stream<String> sText, String initText) {
        this(sText, initText, 15);
    }

    public FxTextField(Stream<String> sText, String initText, int width) {
        this(sText, initText, width, new Cell<>(true));
    }

    public FxTextField(String initText, int width, Cell<Boolean> enabled) {
        this(new Stream<>(), initText, width, enabled);
    }

    public FxTextField(Stream<String> sText, String initText, int width, Cell<Boolean> enabled) {
        super(initText);
        setPrefColumnCount(width);

        allow = sText.map(u -> 1)  // Block local changes until remote change has
                // been completed in the GUI
                .orElse(sDecrement)
                .accum(0, Integer::sum).map(b -> b == 0);

        final StreamSink<String> sUserChangesSnk = new StreamSink<>();
        this.sUserChanges = sUserChangesSnk;
        this.text = sUserChangesSnk.gate(allow).orElse(sText).hold(initText);

        ChangeListener<String> textListener = new ChangeListener<>() {
            private String text = null;

            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                this.text = getText();
                Platform.runLater(() -> {
                    if (this.text != null) {
                        sUserChangesSnk.send(this.text);
                        this.text = null;
                    }
                });
            }
        };
        textProperty().addListener(textListener);

        // Do it at the end of the transaction so it works with looped cells
        Transaction.post(() -> setDisabled(!enabled.sample()));
        addManagedListener(this, () -> sText.listen(text -> Platform.runLater(() -> {
            textProperty().removeListener(textListener);
            setText(text);
            textProperty().addListener(textListener);
            sDecrement.send(-1);  // Re-allow blocked remote changes
        })).append(
                Operational.updates(enabled).listen(ena -> Util.runInFX(() -> this.setDisabled(!ena)))
        ));
    }

    private final StreamSink<Integer> sDecrement = new StreamSink<>();
    private final Cell<Boolean> allow;
    public final Cell<String> text;
    public final Stream<String> sUserChanges;
}

