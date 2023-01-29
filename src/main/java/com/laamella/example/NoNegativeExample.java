package com.laamella.example;

import com.laamella.javafxodium.FxButton;
import com.laamella.javafxodium.FxLabel;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import nz.sodium.CellLoop;
import nz.sodium.Transaction;

public class NoNegativeExample extends Application {
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("nonegative");
        FlowPane pane = Transaction.run(() -> {
            var value = new CellLoop<Integer>();
            var lblValue = new FxLabel(value.map(i -> Integer.toString(i)));
            var plus = new FxButton("+");
            var minus = new FxButton("-");
            var sPlusDelta = plus.sClicked.map(u -> 1);
            var sMinusDelta = minus.sClicked.map(u -> -1);
            var sDelta = sPlusDelta.orElse(sMinusDelta);
            var sUpdate = sDelta
                    .snapshot(value, Integer::sum)
                    .filter(n -> n >= 0);
            value.loop(sUpdate.hold(0));
            return new FlowPane(lblValue, plus, minus);
        });
        primaryStage.setScene(new Scene(pane, 400, 160));
        primaryStage.show();
    }
}

