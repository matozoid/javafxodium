package com.laamella.example;

import com.laamella.javafxodium.FxLabel;
import com.laamella.javafxodium.FxTextField;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

public class LabelExample extends Application {
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("label");
        FxTextField msg = new FxTextField("Hello");
        FxLabel lbl = new FxLabel(msg.text);

        primaryStage.setScene(new Scene(new FlowPane(msg, lbl), 400, 160));
        primaryStage.show();
    }
}
