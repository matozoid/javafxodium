//package com.laamella.javafxodium;
//
//import javafx.scene.control.TextArea;
//import nz.sodium.*;
//
//import javax.swing.*;
//import javax.swing.event.DocumentEvent;
//import javax.swing.event.DocumentListener;
//
//public class FxTextArea extends TextArea {
//    public FxTextArea(String initText, int rows, int columns) {
//        this(new Stream<>(), initText, rows, columns);
//    }
//
//    public FxTextArea(String initText) {
//        this(new Stream<>(), initText);
//    }
//
//    public FxTextArea(Stream<String> sText, String initText, int rows, int columns) {
//        this(sText, initText, rows, columns, new Cell<>(true));
//    }
//
//    public FxTextArea(Stream<String> sText, String initText) {
//        this(sText, initText, new Cell<>(true));
//    }
//
//    public FxTextArea(String initText, int rows, int columns, Cell<Boolean> enabled) {
//        this(new Stream<>(), initText, rows, columns, enabled);
//    }
//
//    public FxTextArea(String initText, Cell<Boolean> enabled) {
//        this(new Stream<>(), initText, enabled);
//    }
//
//    public FxTextArea(Stream<String> sText, String initText, Cell<Boolean> enabled) {
//        super(initText);
//        setup(sText, initText, enabled);
//    }
//
//    public FxTextArea(Stream<String> sText, String initText, int rows, int columns, Cell<Boolean> enabled) {
//        super(initText, rows, columns);
//        setup(sText, initText, enabled);
//    }
//
//    /**
//     * Non-editable text area with text defined by a cell.
//     */
//    public FxTextArea(Cell<String> text) {
//        this(Operational.updates(text), text.sample());
//        setEditable(false);
//    }
//
//    /**
//     * Non-editable text area with text defined by a cell.
//     */
//    public FxTextArea(Cell<String> text, int rows, int columns) {
//        this(Operational.updates(text), text.sample(), rows, columns);
//        setEditable(false);
//    }
//
//    /**
//     * Non-editable text area with text defined by a cell.
//     */
//    public FxTextArea(Cell<String> text, Cell<Boolean> enabled) {
//        this(Operational.updates(text), text.sample(), enabled);
//        setEditable(false);
//    }
//
//    /**
//     * Non-editable text area with text defined by a cell.
//     */
//    public FxTextArea(Cell<String> text, int rows, int columns, Cell<Boolean> enabled) {
//        this(Operational.updates(text), text.sample(), rows, columns, enabled);
//        setEditable(false);
//    }
//
//    private void setup(Stream<String> sText, String initText, Cell<Boolean> enabled) {
//        allow = sText.map(u -> 1).orElse(sDecrement).accum(0, Integer::sum).map(b -> b == 0);
//
//        final StreamSink<String> sUserText = new StreamSink<>();
//        this.text = sUserText.gate(allow).orElse(sText).hold(initText);
//        DocumentListener dl = new DocumentListener() {
//            public void changedUpdate(DocumentEvent e) {
//                update();
//            }
//
//            public void removeUpdate(DocumentEvent e) {
//                update();
//            }
//
//            public void insertUpdate(DocumentEvent e) {
//                update();
//            }
//
//            public void update() {
//                sUserText.send(getText());
//            }
//        };
//
//        getDocument().addDocumentListener(dl);
//
//        // Do it at the end of the transaction so it works with looped cells
//        Transaction.post(() -> setEnabled(enabled.sample()));
//        l = sText.listen(text -> SwingUtilities.invokeLater(() -> {
//            setText(text);
//            sDecrement.send(-1);
//        })).append(
//                Operational.updates(enabled).listen(
//                        ena -> {
//                            if (SwingUtilities.isEventDispatchThread())
//                                this.setEnabled(ena);
//                            else {
//                                SwingUtilities.invokeLater(() -> this.setEnabled(ena));
//                            }
//                        }
//                )
//        );
//    }
//
//    private final StreamSink<Integer> sDecrement = new StreamSink<>();
//    private Cell<Boolean> allow;
//    private Listener l;
//    public Cell<String> text;
//
//    public void removeNotify() {
//        l.unlisten();
//        super.removeNotify();
//    }
//}
//
