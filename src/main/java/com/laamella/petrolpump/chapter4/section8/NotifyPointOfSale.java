package com.laamella.petrolpump.chapter4.section8;

import com.laamella.petrolpump.chapter4.section4.LifeCycle;
import com.laamella.petrolpump.chapter4.section7.Fill;
import com.laamella.petrolpump.pump.Fuel;
import com.laamella.petrolpump.pump.Sale;
import io.vavr.control.Option;
import nz.sodium.Cell;
import nz.sodium.CellLoop;
import nz.sodium.Stream;
import nz.sodium.Unit;

import static com.laamella.petrolpump.chapter4.section4.LifeCycle.*;

public class NotifyPointOfSale {
    public final Stream<Fuel> sStart;
    public final Cell<Option<Fuel>> fillActive;
    public final Cell<Option<Fuel>> fuelFlowing;
    public final Stream<End> sEnd;
    public final Stream<Unit> sBeep;
    public final Stream<Sale> sSaleComplete;

    private enum Phase {IDLE, FILLING, POS}

    public NotifyPointOfSale(
            LifeCycle lc,
            Stream<Unit> sClearSale,
            Fill fi) {
        CellLoop<Phase> phase = new CellLoop<>();
        sStart = lc.sStart.gate(phase.map(p -> p == Phase.IDLE));
        sEnd = lc.sEnd.gate(phase.map(p -> p == Phase.FILLING));
        phase.loop(
                sStart.map(u -> Phase.FILLING)
                        .orElse(sEnd.map(u -> Phase.POS))
                        .orElse(sClearSale.map(u -> Phase.IDLE))
                        .hold(Phase.IDLE));
        fuelFlowing =
                sStart.map(Option::some).orElse(
                        sEnd.map(f -> Option.none())).hold(Option.none());
        fillActive =
                sStart.map(Option::some).orElse(
                        sClearSale.map(f -> Option.none())).hold(Option.none());
        sBeep = sClearSale;
        sSaleComplete = Stream.filterOptional(sEnd.snapshot(
                fuelFlowing.lift(fi.price, fi.dollarsDelivered,
                        fi.litersDelivered,
                        (oFuel, price_, dollars, liters) ->
                                oFuel.isDefined() ? Option.some(
                                        new Sale(oFuel.get(), price_, dollars, liters))
                                        : Option.none())
        ));
    }
}

