package com.laamella.example;

import com.laamella.javafxodium.FxButton;
import com.laamella.javafxodium.FxLabel;
import com.laamella.javafxodium.FxTextField;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import nz.sodium.Cell;
import nz.sodium.Stream;

public class TranslateExample extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Translate");
        FxTextField english = new FxTextField("I like FRP");
        FxButton translate = new FxButton("Translate");
        Stream<String> sLatin =
                translate.sClicked.snapshot(english.text, (u, txt) ->
                        txt.trim().replaceAll(" |$", "us ").trim()
                );
        Cell<String> latin = sLatin.hold("");
        FxLabel lblLatin = new FxLabel(latin);
        primaryStage.setScene(new Scene(new FlowPane(english, translate, lblLatin), 400, 160));
        primaryStage.show();
    }
}

