package com.laamella.example;

import com.laamella.javafxodium.FxButton;
import com.laamella.javafxodium.FxLabel;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import nz.sodium.Cell;
import nz.sodium.Stream;

public class RedGreenExample extends Application {
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("redgreen");
        FxButton red = new FxButton("red");
        FxButton green = new FxButton("green");
        Stream<String> sRed = red.sClicked.map(u -> "red");
        Stream<String> sGreen = green.sClicked.map(u -> "green");
        Stream<String> sColor = sRed.orElse(sGreen);
        Cell<String> color = sColor.hold("");
        FxLabel lbl = new FxLabel(color);

        primaryStage.setScene(new Scene(new FlowPane(red, green, lbl), 400, 160));
        primaryStage.show();
    }
}

