package com.laamella.javafxodium;

import io.vavr.control.Option;
import javafx.collections.ObservableList;
import javafx.scene.layout.HBox;
import nz.sodium.Cell;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import static javafx.collections.FXCollections.observableList;

public class FxDateField extends HBox {
    public final Cell<LocalDate> date;

    public FxDateField() {
        this(LocalDate.now());
    }

    private static final List<String> months = Arrays.asList(
            "",
            "Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    );

    public FxDateField(LocalDate cal) {
        ObservableList<Integer> years = observableList(new ArrayList<>());
        LocalDate now = LocalDate.now();
        for (int y = now.getYear() - 10; y <= now.getYear() + 10; y++) {
            years.add(y);
        }
        FxComboBox<Integer> year = new FxComboBox<>(years);
        year.setValue(cal.getYear());
        FxComboBox<String> month = new FxComboBox<>(observableList(months));
        Vector<Integer> days = new Vector<>();
        for (int d = 1; d <= 31; d++) {
            days.add(d);
        }
        month.setValue(months.get(cal.getMonthValue()));
        FxComboBox<Integer> day = new FxComboBox<>(observableList(days));
        day.setValue(cal.getDayOfMonth());

        getChildren().addAll(year, month, day);

        Cell<Option<Integer>> monthIndex = month.selectedItem.map(
                ostr -> {
                    if (ostr.isDefined()) {
                        for (int i = 0; i < months.size(); i++)
                            if (months.get(i).equals(ostr.get()))
                                return Option.some(i);
                    }
                    return Option.none();
                });
        date = year.selectedItem.lift(monthIndex, day.selectedItem,
                (oy, om, od) -> oy.isDefined() && om.isDefined() && od.isDefined()
                        ? LocalDate.of(oy.get(), om.get(), od.get())
                        : LocalDate.MIN);
    }
}

