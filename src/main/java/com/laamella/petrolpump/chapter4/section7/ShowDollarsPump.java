package com.laamella.petrolpump.chapter4.section7;

import com.laamella.petrolpump.chapter4.section4.LifeCycle;
import com.laamella.petrolpump.pump.*;
import io.vavr.control.Option;
import nz.sodium.Cell;
import nz.sodium.Unit;

public class ShowDollarsPump implements Pump {
    public Outputs create(Inputs inputs) {
        LifeCycle lc = new LifeCycle(inputs.sNozzle1, inputs.sNozzle2, inputs.sNozzle3);
        Fill fi = new Fill(lc.sStart.map(u -> Unit.UNIT), inputs.sFuelPulses, inputs.calibration, inputs.price1, inputs.price2, inputs.price3, lc.sStart);
        return new Outputs()
                .setDelivery(lc.fillActive.map(
                        of ->
                                of.equals(Option.some(Fuel.ONE)) ? Delivery.FAST1 :
                                        of.equals(Option.some(Fuel.TWO)) ? Delivery.FAST2 :
                                                of.equals(Option.some(Fuel.THREE)) ? Delivery.FAST3 :
                                                        Delivery.OFF))
                .setSaleCostLCD(fi.dollarsDelivered.map(Formatters::formatSaleCost))
                .setSaleQuantityLCD(fi.litersDelivered.map(Formatters::formatSaleQuantity))
                .setPriceLCD1(priceLCD(lc.fillActive, fi.price, Fuel.ONE, inputs))
                .setPriceLCD2(priceLCD(lc.fillActive, fi.price, Fuel.TWO, inputs))
                .setPriceLCD3(priceLCD(lc.fillActive, fi.price, Fuel.THREE, inputs));
    }

    public static Cell<String> priceLCD(
            Cell<Option<Fuel>> fillActive,
            Cell<Double> fillPrice,
            Fuel fuel,
            Inputs inputs) {
        Cell<Double> idlePrice = switch (fuel) {
            case ONE -> inputs.price1;
            case TWO -> inputs.price2;
            case THREE -> inputs.price3;
        };
        return fillActive.lift(fillPrice, idlePrice,
                (oFuelSelected, fillPrice_, idlePrice_) ->
                        oFuelSelected.isDefined()
                                ? oFuelSelected.get() == fuel
                                ? Formatters.formatPrice(fillPrice_)
                                : ""
                                : Formatters.formatPrice(idlePrice_));
    }
}

