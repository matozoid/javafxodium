package com.laamella.example;

import com.laamella.javafxodium.FxButton;
import com.laamella.javafxodium.FxTextField;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import nz.sodium.Stream;

public class GameChatExample extends Application {
    @Override
    public void start(Stage primaryStage)  {
        primaryStage.setTitle("gamechat");
        FxButton onegai = new FxButton("Onegai shimasu");
        FxButton thanks = new FxButton("Thank you");
        Stream<String> sOnegai = onegai.sClicked.map(u -> "Onegai shimasu");
        Stream<String> sThanks = thanks.sClicked.map(u -> "Thank you");
        Stream<String> sCanned = sOnegai.orElse(sThanks);
        FxTextField text = new FxTextField(sCanned, "");

        primaryStage.setScene(new Scene(new FlowPane(text, onegai, thanks), 400,160));
        primaryStage.show();

    }
}

