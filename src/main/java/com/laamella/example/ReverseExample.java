package com.laamella.example;

import com.laamella.javafxodium.FxLabel;
import com.laamella.javafxodium.FxTextField;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import nz.sodium.Cell;

public class ReverseExample extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("reverse");
        FxTextField msg = new FxTextField("Hello");
        Cell<String> reversed = msg.text
                .map(t -> new StringBuilder(t).reverse().toString());
        FxLabel lbl = new FxLabel(reversed);
        primaryStage.setScene(new Scene(new FlowPane(msg, lbl), 400, 160));
        primaryStage.show();
    }
}

