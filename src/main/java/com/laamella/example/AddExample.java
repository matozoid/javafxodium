package com.laamella.example;

import com.laamella.javafxodium.FxLabel;
import com.laamella.javafxodium.FxTextField;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import nz.sodium.Cell;

public class AddExample extends Application {
    private static Integer parseInt(String t) {
        try {
            return Integer.parseInt(t);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("add");
        var txtA = new FxTextField("5");
        var txtB = new FxTextField("10");
        var a = txtA.text.map(AddExample::parseInt);
        var b = txtB.text.map(AddExample::parseInt);
        var sum = a.lift(b, Integer::sum);
        var lblSum = new FxLabel(sum.map(i -> Integer.toString(i)));
        var frame = new FlowPane(txtA,txtB,lblSum);
        var scene = new Scene(frame, 400,160);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}

