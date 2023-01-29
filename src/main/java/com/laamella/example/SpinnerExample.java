package com.laamella.example;

import com.laamella.javafxodium.FxButton;
import com.laamella.javafxodium.FxLabel;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import nz.sodium.CellLoop;
import nz.sodium.Stream;
import nz.sodium.Transaction;

public class SpinnerExample extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("spinner");
        FlowPane view = Transaction.run(() -> {
            CellLoop<Integer> value = new CellLoop<>();
            FxLabel lblValue = new FxLabel(
                    value.map(i -> Integer.toString(i)));
            FxButton plus = new FxButton("+");
            FxButton minus = new FxButton("-");
            Stream<Integer> sPlusDelta = plus.sClicked.map(u -> 1);
            Stream<Integer> sMinusDelta = minus.sClicked.map(u -> -1);
            Stream<Integer> sDelta = sPlusDelta.orElse(sMinusDelta);
            Stream<Integer> sUpdate = sDelta.snapshot(value, Integer::sum);
            value.loop(sUpdate.hold(0));
            return new FlowPane(lblValue, plus, minus);
        });
        primaryStage.setScene(new Scene(view, 400, 160));
        primaryStage.show();
    }
}

