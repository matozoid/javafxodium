package com.laamella.petrolpump.pump;

import com.laamella.javafxodium.FxButton;
import com.laamella.javafxodium.FxComboBox;
import com.laamella.javafxodium.FxTextField;
import io.vavr.Function1;
import io.vavr.Tuple2;
import io.vavr.control.Option;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Callback;
import nz.sodium.*;
import nz.sodium.Cell;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.laamella.javafxodium.ListenerManager.addManagedListener;
import static com.laamella.javafxodium.Util.classpathUrl;
import static javafx.scene.media.MediaPlayer.INDEFINITE;


class PumpFace extends StackPane {
    private final Image background;
    private final Canvas canvas = new Canvas();
    private final Image[] smalls = new Image[8];
    private final Image[] larges = new Image[8];
    private final Image[] nozzleImgs = new Image[3];
    private final Cell<List<Integer>> presetLCD;
    private final Cell<List<Integer>> saleCostLCD;
    private final Cell<List<Integer>> saleQuantityLCD;
    private final Cell<List<Integer>> priceLCD1;
    private final Cell<List<Integer>> priceLCD2;
    private final Cell<List<Integer>> priceLCD3;
    private final Cell<UpDown>[] nozzles = new Cell[3];
    public final Cell<Rectangle>[] nozzleRects = new Cell[3];

    PumpFace(
            StreamSink<Point2D> sClick,
            Cell<List<Integer>> presetLCD,
            Cell<List<Integer>> saleCostLCD,
            Cell<List<Integer>> saleQuantityLCD,
            Cell<List<Integer>> priceLCD1,
            Cell<List<Integer>> priceLCD2,
            Cell<List<Integer>> priceLCD3,
            Cell<UpDown> nozzle1,
            Cell<UpDown> nozzle2,
            Cell<UpDown> nozzle3
    ) throws IOException {
        setOnMousePressed(event -> sClick.send(new Point2D(event.getX(), event.getY())));
        this.presetLCD = presetLCD;
        this.saleCostLCD = saleCostLCD;
        this.saleQuantityLCD = saleQuantityLCD;
        this.priceLCD1 = priceLCD1;
        this.priceLCD2 = priceLCD2;
        this.priceLCD3 = priceLCD3;
        this.nozzles[0] = nozzle1;
        this.nozzles[1] = nozzle2;
        this.nozzles[2] = nozzle3;
        addManagedListener(this, () -> new Listener()
                .append(presetLCD.listen(text -> Transaction.post(() -> this.repaintSegments(193, 140, larges, 5))))
                .append(saleCostLCD.listen(text -> Transaction.post(() -> this.repaintSegments(517, 30, larges, 5))))
                .append(saleQuantityLCD.listen(text -> Transaction.post(() -> this.repaintSegments(517, 120, larges, 5))))
                .append(priceLCD1.listen(text -> Transaction.post(() -> this.repaintSegments(355, 230, smalls, 4))))
                .append(priceLCD2.listen(text -> Transaction.post(() -> this.repaintSegments(485, 230, smalls, 4))))
                .append(priceLCD3.listen(text -> Transaction.post(() -> this.repaintSegments(615, 230, smalls, 4))))
                .append(nozzle1.listen(ud -> Transaction.post(this::paintSegments)))
                .append(nozzle2.listen(ud -> Transaction.post(this::paintSegments)))
                .append(nozzle3.listen(ud -> Transaction.post(this::paintSegments))));
        background = new Image(classpathUrl("/images/petrol-pump-front.png"));

        for (int i = 0; i < 8; i++) {
            smalls[i] = new Image(classpathUrl("/images/small" + i + ".png"));
            larges[i] = new Image(classpathUrl("/images/large" + i + ".png"));
        }
        for (int i = 0; i < 3; i++) {
            nozzleImgs[i] = new Image(classpathUrl("/images/nozzle" + (i + 1) + ".png"));
            final int x = 270 + i * 130;
            final double width = nozzleImgs[i].getWidth();
            final double height = nozzleImgs[i].getHeight();
            nozzleRects[i] = nozzles[i].map(upDown -> {
                        Rectangle rectangle = new Rectangle(x, upDown == UpDown.UP ? 300 : 330, width, height);
                        System.out.println("***" + rectangle);
                        return rectangle;
                    }
            );
        }

        var children = getChildren();
        children.add(new ImageView(background));
        children.add(canvas);
        canvas.setHeight(background.getHeight());
        canvas.setWidth(background.getWidth());
    }

    public void paintSegments() {
        GraphicsContext g = canvas.getGraphicsContext2D();
        g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        Transaction.runVoid(() -> {
            drawSegments(g, 193, 140, presetLCD.sample(), larges, 5);
            drawSegments(g, 517, 30, saleCostLCD.sample(), larges, 5);
            drawSegments(g, 517, 120, saleQuantityLCD.sample(), larges, 5);
            drawSegments(g, 355, 230, priceLCD1.sample(), smalls, 4);
            drawSegments(g, 485, 230, priceLCD2.sample(), smalls, 4);
            drawSegments(g, 615, 230, priceLCD3.sample(), smalls, 4);
            for (int i = 0; i < 3; i++) {
                Rectangle r = nozzleRects[i].sample();
                System.out.println(r);
                g.drawImage(nozzleImgs[i], r.getX(), r.getY());
            }
        });
    }

    private static Rectangle lcdBounds(int ox, int oy, Image[] images, int noOfDigits) {
        double w = images[0].getWidth();
        double h = images[0].getHeight();
        return new Rectangle(ox - w * noOfDigits, oy, w * noOfDigits, h);
    }

    private void repaintSegments(int ox, int oy, Image[] images, int noOfDigits) {
        Rectangle r = lcdBounds(ox, oy, images, noOfDigits);
    }

    public static void drawSegments(GraphicsContext g, int ox, int oy, List<Integer> digits, Image[] images, int noOfDigits) {
        for (int i = 0; i < digits.size() && i < noOfDigits; i++) {
            double x = ox - images[0].getWidth() * (i + 1);
            int digit = digits.get(digits.size() - 1 - i);
            for (int j = 0; j < 8; j++)
                if ((digit & (1 << j)) != 0)
                    g.drawImage(images[j], x, oy);
        }
    }
}

class ClassNameCellFactory implements Callback<ListView<Pump>, ListCell<Pump>> {

    @Override
    public ListCell<Pump> call(ListView<Pump> param) {
        return new ListCell<>() {
            @Override
            protected void updateItem(Pump item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null)
                    setText(item.getClass().getName());
            }
        };
    }
}

public class PetrolPump extends Application {
    private Stream<Key> sKey;
    public StreamSink<Integer> sFuelPulses = new StreamSink<>();
    public Cell<Delivery> delivery;

    private static Cell<List<Integer>> format7Seg(Cell<String> text, int digits) {
        return text.map(text_ -> {
            Integer[] segs = new Integer[digits];
            for (int i = 0; i < digits; i++)
                segs[i] = 0;
            int i = digits - 1;
            int j = text_.length() - 1;
            while (j >= 0 && i >= 0) {
                char ch = text_.charAt(j);
                switch (ch) {
                    case '-' -> {
                        segs[i] |= 0x08;
                        i--;
                    }
                    case '0' -> {
                        segs[i] |= 0x77;
                        i--;
                    }
                    case '1' -> {
                        segs[i] |= 0x24;
                        i--;
                    }
                    case '2' -> {
                        segs[i] |= 0x6b;
                        i--;
                    }
                    case '3' -> {
                        segs[i] |= 0x6d;
                        i--;
                    }
                    case '4' -> {
                        segs[i] |= 0x3c;
                        i--;
                    }
                    case '5' -> {
                        segs[i] |= 0x5d;
                        i--;
                    }
                    case '6' -> {
                        segs[i] |= 0x5f;
                        i--;
                    }
                    case '7' -> {
                        segs[i] |= 0x64;
                        i--;
                    }
                    case '8' -> {
                        segs[i] |= 0x7f;
                        i--;
                    }
                    case '9' -> {
                        segs[i] |= 0x7c;
                        i--;
                    }
                    case '.' -> segs[i] |= 0x80;
                }
                j--;
            }
            return Arrays.asList(segs);
        });
    }

    public static <A> Stream<A> changes(Cell<A> b) {
        return Stream.filterOptional(
                Operational.value(b).snapshot(b, (neu, old) ->
                        old.equals(neu) ? Option.none() : Option.some(neu)));
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Functional Reactive Petrol Pump");
        BorderPane borderPane = new BorderPane();

        Transaction.runVoid(() -> {
            try {
                GridPane topTwoPanels = new GridPane();
                borderPane.setTop(topTwoPanels);
                FlowPane firstPanel = new FlowPane();
                topTwoPanels.add(firstPanel, 0, 0);
                FlowPane secondPanel = new FlowPane();
                topTwoPanels.add(secondPanel, 0, 1);
                firstPanel.getChildren().add(new Label("Logic"));

                FxComboBox<Pump> logic = new FxComboBox<>(FXCollections.observableArrayList(
                        new com.laamella.petrolpump.chapter4.section4.LifeCyclePump(),
                        new com.laamella.petrolpump.chapter4.section6.AccumulatePulsesPump(),
                        new com.laamella.petrolpump.chapter4.section7.ShowDollarsPump(),
                        new com.laamella.petrolpump.chapter4.section8.ClearSalePump(),
                        new com.laamella.petrolpump.chapter4.section9.KeypadPump(),
                        new com.laamella.petrolpump.chapter4.section11.PresetAmountPump()
                ));
                logic.setCellFactory(new ClassNameCellFactory());
                firstPanel.getChildren().add(logic);
                secondPanel.getChildren().add(new Label("Price1"));
                FxTextField textPrice1 = new FxTextField("2.149", 7);
                secondPanel.getChildren().add(textPrice1);
                secondPanel.getChildren().add(new Label("Price2"));
                FxTextField textPrice2 = new FxTextField("2.341", 7);
                secondPanel.getChildren().add(textPrice2);
                secondPanel.getChildren().add(new Label("Price3"));
                FxTextField textPrice3 = new FxTextField("1.499", 7);
                secondPanel.getChildren().add(textPrice3);

                Function1<String, Double> parseDbl = str -> {
                    try {
                        return Double.parseDouble(str);
                    } catch (NumberFormatException e) {
                        return 0.0;
                    }
                };

                // An event of mouse presses
                StreamSink<Point2D> sClick = new StreamSink<>();

                sKey = toKey(sClick);

                Integer[] five = {0xff, 0xff, 0xff, 0xff, 0xff};
                List<Integer> five8s = Arrays.asList(five);
                Integer[] four = {0xff, 0xff, 0xff, 0xff};
                List<Integer> four8s = Arrays.asList(four);
                @SuppressWarnings("unchecked")
                CellLoop<UpDown>[] nozzles = new CellLoop[3];
                for (int i = 0; i < 3; i++)
                    nozzles[i] = new CellLoop<>();

                Cell<Double> calibration = new Cell<>(0.001);
                Cell<Double> price1 = textPrice1.text.map(parseDbl);
                Cell<Double> price2 = textPrice2.text.map(parseDbl);
                Cell<Double> price3 = textPrice3.text.map(parseDbl);
                CellSink<Stream<Unit>> csClearSale = new CellSink<>(new Stream<>());
                Stream<Unit> sClearSale = Cell.switchS(csClearSale);

                Cell<Outputs> outputs = logic.selectedItem.map(
                        pump -> pump.getOrElse(NoPump::new).create(
                                new Inputs(
                                        Operational.updates(nozzles[0]),
                                        Operational.updates(nozzles[1]),
                                        Operational.updates(nozzles[2]),
                                        sKey,
                                        sFuelPulses,
                                        calibration,
                                        price1,
                                        price2,
                                        price3,
                                        sClearSale
                                )
                        )
                );

                delivery = Cell.switchC(outputs.map(o -> o.delivery));
                Cell<String> presetLCD = Cell.switchC(outputs.map(o -> o.presetLCD));
                Cell<String> saleCostLCD = Cell.switchC(outputs.map(o -> o.saleCostLCD));
                Cell<String> saleQuantityLCD = Cell.switchC(outputs.map(o -> o.saleQuantityLCD));
                Cell<String> priceLCD1 = Cell.switchC(outputs.map(o -> o.priceLCD1));
                Cell<String> priceLCD2 = Cell.switchC(outputs.map(o -> o.priceLCD2));
                Cell<String> priceLCD3 = Cell.switchC(outputs.map(o -> o.priceLCD3));
                Stream<Unit> sBeep = Cell.switchS(outputs.map(o -> o.sBeep));
                Stream<Sale> sSaleComplete = Cell.switchS(outputs.map(o -> o.sSaleComplete));

                AudioClip beepClip = new AudioClip(classpathUrl("/sounds/beep.wav"));
                Listener l = new Listener().append(sBeep.listen(u -> {
                    beepClip.play();
                }));

                MediaPlayer fastRumble = new MediaPlayer(new Media(classpathUrl("/sounds/fast.wav")));
                fastRumble.setCycleCount(INDEFINITE);
                AudioClip slowRumble = new AudioClip(classpathUrl("/sounds/slow.wav"));
                slowRumble.setCycleCount(INDEFINITE);

                l = l.append(changes(delivery).listen(d -> {
                    switch (d) {
                        case FAST1, FAST2, FAST3 -> fastRumble.play();
                        default -> fastRumble.stop();
                    }
                    switch (d) {
                        case SLOW1, SLOW2, SLOW3 -> slowRumble.play();
                        default -> slowRumble.stop();
                    }
                }));

                PumpFace face = new PumpFace(
                        sClick,
                        format7Seg(presetLCD, 5),
                        format7Seg(saleCostLCD, 5),
                        format7Seg(saleQuantityLCD, 5),
                        format7Seg(priceLCD1, 4),
                        format7Seg(priceLCD2, 4),
                        format7Seg(priceLCD3, 4),
                        nozzles[0],
                        nozzles[1],
                        nozzles[2]
                );
                borderPane.setCenter(face);
                for (int i = 0; i < 3; i++) {
                    final Cell<Tuple2<Rectangle, UpDown>> rect_state = face.nozzleRects[i].lift(nozzles[i], Tuple2::new);
                    nozzles[i].loop(
                            Stream.filterOptional(
                                    sClick.snapshot(rect_state,
                                            (pt, rs) -> {
                                                Option<UpDown> upDowns = rs._1.contains(pt) ? Option.some(invert(rs._2))
                                                        : Option.none();
                                                System.out.println(pt + " " + rs + " -> " + upDowns);
                                                return upDowns;
                                            }
                                    )
                            ).hold(UpDown.DOWN)
                    );
                }

                Listener append = l.append(sSaleComplete.listen(sale -> {
                    Platform.runLater(() -> {
                        Dialog<Void> dialog = new Dialog<>();
                        dialog.setTitle("Sale complete");
                        GridPane gridPane = new GridPane();
                        dialog.getDialogPane().setContent(gridPane);
                        gridPane.add(new Label("Fuel "), 0, 0);
                        gridPane.add(new Label(sale.fuel.toString()), 1, 0);
                        gridPane.add(new Label("Price "), 0, 1);
                        gridPane.add(new Label(Formatters.priceFmt.format(sale.price)), 1, 1);
                        gridPane.add(new Label("Dollars delivered "), 0, 2);
                        gridPane.add(new Label(Formatters.costFmt.format(sale.cost)), 1, 2);
                        gridPane.add(new Label("Liters delivered "), 0, 3);
                        gridPane.add(new Label(Formatters.quantityFmt.format(sale.quantity)), 1, 3);
                        FxButton ok = new FxButton("OK");


                        ButtonType loginButtonType = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);
//                        Dialog<String> dialog = new Dialog<>();
//                        dialog.setTitle("Login Dialog");
//                        dialog.setContentText("Would you like to log in?");
                        dialog.getDialogPane().getButtonTypes().add(loginButtonType);
//                        boolean disabled = false; // computed based on content of text fields, for example 
//                         dialog.getDialogPane().lookupButton(loginButtonType).setDisable(disabled);


//                        gridPane.add(ok, 0, 4, 2, 1);
                        dialog.showAndWait();
                        csClearSale.send(ok.sClicked);
//                        addManagedListener(gridPane, () -> ok.sClicked.listen(u -> dialog.close()));
                    });
                }));
                addManagedListener(borderPane, () -> append);
            } catch (IOException e) {
                System.err.println("Unexpected exception: " + e);
            }
        });

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        primaryStage.setOnShowing(event ->
                executorService.submit((Runnable) () -> {
                    // Simulate fuel pulses when 'delivery' is on.
                    while (true) {
                        Transaction.runVoid(() -> {
                            switch (delivery.sample()) {
                                case FAST1, FAST2, FAST3 -> sFuelPulses.send(40);
                                case SLOW1, SLOW2, SLOW3 -> sFuelPulses.send(2);
                            }
                        });
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                        }
                    }
                }));
        primaryStage.setOnCloseRequest(event -> System.exit(0));
        primaryStage.setScene(new Scene(borderPane));
        primaryStage.show();
    }

    private static UpDown invert(UpDown u) {
        return u == UpDown.UP ? UpDown.DOWN : UpDown.UP;
    }

    public static Stream<Key> toKey(Stream<Point2D> sClick) {
        HashMap<Tuple2<Integer, Integer>, Key> keys = new HashMap<>();
        keys.put(new Tuple2<>(0, 0), Key.ONE);
        keys.put(new Tuple2<>(1, 0), Key.TWO);
        keys.put(new Tuple2<>(2, 0), Key.THREE);
        keys.put(new Tuple2<>(0, 1), Key.FOUR);
        keys.put(new Tuple2<>(1, 1), Key.FIVE);
        keys.put(new Tuple2<>(2, 1), Key.SIX);
        keys.put(new Tuple2<>(0, 2), Key.SEVEN);
        keys.put(new Tuple2<>(1, 2), Key.EIGHT);
        keys.put(new Tuple2<>(2, 2), Key.NINE);
        keys.put(new Tuple2<>(1, 3), Key.ZERO);
        keys.put(new Tuple2<>(2, 3), Key.CLEAR);

        return Stream.filterOptional(sClick.map(pt -> {
            int x = (int) (pt.getX() - 40);
            int y = (int) (pt.getY() - 230);
            int col = x / 50;
            int row = y / 50;
            boolean valid =
                    x >= 0 && x % 50 < 40 &&
                            y >= 0 && y % 50 < 40 &&
                            col < 3 && row < 4;
            Key key = valid ? keys.get(new Tuple2<>(col, row)) : null;
            return Option.of(key);
        }));
    }

    public static void main(String[] args) {
        launch(args);
    }
}

