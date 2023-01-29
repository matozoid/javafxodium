package com.laamella.javafxodium;

import javafx.scene.layout.GridPane;
import nz.sodium.Cell;
import nz.sodium.Stream;
import nz.sodium.StreamLoop;

public class FxSpinner extends GridPane {
    public FxSpinner(int initialValue) {
        StreamLoop<Integer> sSetValue = new StreamLoop<>();
        FxTextField textField = new FxTextField(
                sSetValue.map(v -> Integer.toString(v)),
                Integer.toString(initialValue),
                5);
        this.value = textField.text.map(txt -> {
            try {
                return Integer.parseInt(txt);
            } catch (NumberFormatException e) {
                return 0;
            }
        });
        FxButton plus = new FxButton("+");
        FxButton minus = new FxButton("-");

        add(textField, 0, 0, 1, 2);
        add(plus, 1, 0, 1, 1);
        add(minus, 1, 1, 1, 1);

        Stream<Integer> sPlusDelta = plus.sClicked.map(u -> 1);
        Stream<Integer> sMinusDelta = minus.sClicked.map(u -> -1);
        Stream<Integer> sDelta = sPlusDelta.orElse(sMinusDelta);
        sSetValue.loop(
                sDelta.snapshot(
                        this.value,
                        Integer::sum
                ));
    }

    public final Cell<Integer> value;
}

