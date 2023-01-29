package com.laamella.example;

import com.laamella.javafxodium.FxSpinner;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import nz.sodium.Transaction;

public class SpinMeExample extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("spinme");
        FlowPane view = new FlowPane();
        Transaction.runVoid(() -> {
            FxSpinner spnr = new FxSpinner(0);
            view.getChildren().add(spnr);
        });
        primaryStage.setScene(new Scene(view, 400, 160));
        primaryStage.show();
    }
}

