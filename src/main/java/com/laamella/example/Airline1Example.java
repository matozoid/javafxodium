package com.laamella.example;

import com.laamella.javafxodium.FxButton;
import com.laamella.javafxodium.FxDateField;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import nz.sodium.Cell;

public class Airline1Example extends Application {
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("airline1");

        var departure = new FxDateField();
        var retur = new FxDateField();
        var valid = departure.date.lift(retur.date, (d, r) -> r.isAfter(d));
        var ok = new FxButton("OK", valid);

        var gridbag = new GridPane();
        gridbag.add(new Label("departure"), 0, 0, 1, 1);
        gridbag.add(departure, 1, 0, 1, 1);
        gridbag.add(new Label("return"), 0, 1, 1, 1);
        gridbag.add(retur, 1, 1, 1, 1);
        gridbag.add(ok, 0, 2, 2, 1);

        primaryStage.setScene(new Scene(gridbag, 380, 140));
        primaryStage.show();
    }
}

