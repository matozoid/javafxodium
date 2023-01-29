package com.laamella.example;

import com.laamella.javafxodium.FxButton;
import com.laamella.javafxodium.FxLabel;
import com.laamella.javafxodium.FxSpinner;
import com.laamella.javafxodium.FxTextField;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import nz.sodium.Cell;
import nz.sodium.Transaction;

import java.lang.reflect.Array;

public class FormValidationExample extends Application {
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("formvalidation");
        GridPane gridPane = Transaction.run(() -> {
            final int maxEmails = 4;

            Label[] labels = new Label[maxEmails + 2];
            Node[] fields = new Node[maxEmails + 2];
            Cell<String>[] valids = (Cell<String>[]) Array.newInstance(Cell.class, maxEmails + 2);
            int row = 0;

            labels[row] = new Label("Name");
            FxTextField name = new FxTextField("", 30);
            fields[row] = name;
            valids[row] = name.text.map(t ->
                    t.trim().equals("") ? "<-- enter something" :
                            t.trim().indexOf(' ') < 0 ? "<-- must contain space" :
                                    "");
            row++;

            labels[row] = new Label("No of email addresses");
            FxSpinner number = new FxSpinner(1);
            fields[row] = number;
            valids[row] = number.value.map(n ->
                    n < 1 || n > maxEmails ? "<-- must be 1 to " + maxEmails
                            : "");
            row++;

            for (int i = 0; i < maxEmails; i++, row++) {
                labels[row] = new Label("Email #" + (i + 1));
                final int ii = i;
                Cell<Boolean> enabled = number.value.map(n -> ii < n);
                FxTextField email = new FxTextField("", 30, enabled);
                fields[row] = email;
                valids[row] = email.text.lift(number.value, (e, n) ->
                        ii >= n ? "" :
                                e.trim().equals("") ? "<-- enter something" :
                                        e.indexOf('@') < 0 ? "<-- must contain @" :
                                                "");
            }


            GridPane gridbag = new GridPane();
            Cell<Boolean> allValid = new Cell<>(true);
            for (int i = 0; i < row; i++) {
                gridbag.add(labels[i], 0, i, 1, 1);
                gridbag.add(fields[i], 1, i, 1, 1);
                gridbag.add(new FxLabel(valids[i]), 2, i, 1, 1);
                Cell<Boolean> thisValid = valids[i].map(t -> t.equals(""));
                allValid = allValid.lift(thisValid, (a, b) -> a && b);
            }
            gridbag.add(new FxButton("OK", allValid), 0, row, 3, 1);
            return gridbag;
        });
        primaryStage.setScene(new Scene(gridPane, 600, 200));
        primaryStage.show();
    }
}

