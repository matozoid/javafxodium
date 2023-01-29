package com.laamella.example;

import com.laamella.javafxodium.FxButton;
import com.laamella.javafxodium.FxTextField;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import nz.sodium.Stream;

public class ClearFieldExample extends Application {
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("clearfield");
        FxButton clear = new FxButton("Clear");
        Stream<String> sClearIt = clear.sClicked.map(u -> "");
        FxTextField text = new FxTextField(sClearIt, "Hello");

        primaryStage.setScene(new Scene(new FlowPane(text, clear), 400, 160));
        primaryStage.show();
    }
}

