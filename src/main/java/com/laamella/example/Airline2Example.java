package com.laamella.example;

import com.laamella.javafxodium.FxButton;
import com.laamella.javafxodium.FxDateField;
import io.vavr.Function2;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import nz.sodium.Cell;

import java.time.LocalDate;

class Rule {
    public Rule(Function2<LocalDate, LocalDate, Boolean> f) {
        this.f = f;
    }

    public final Function2<LocalDate, LocalDate, Boolean> f;

    public Cell<Boolean> reify(Cell<LocalDate> dep, Cell<LocalDate> ret) {
        return dep.lift(ret, f);
    }

    public Rule and(Rule other) {
        return new Rule(
                (d, r) -> this.f.apply(d, r) && other.f.apply(d, r)
        );
    }
}

public class Airline2Example extends Application {
    private static boolean unlucky(LocalDate dt) {
        int day = dt.getDayOfMonth();
        return day == 4 || day == 14 || day == 24;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("airline2");

        var departure = new FxDateField();
        var retur = new FxDateField();

        var r1 = new Rule((LocalDate d, LocalDate r) -> r.isAfter(d));
        var r2 = new Rule((d, r) -> !unlucky(d) && !unlucky(r));
        var r = r1.and(r2);
        var valid = r.reify(departure.date, retur.date);

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

