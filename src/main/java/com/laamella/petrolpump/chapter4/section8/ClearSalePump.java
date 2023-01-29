package com.laamella.petrolpump.chapter4.section8;

import com.laamella.petrolpump.chapter4.section4.LifeCycle;
import com.laamella.petrolpump.chapter4.section7.Fill;
import com.laamella.petrolpump.chapter4.section7.ShowDollarsPump;
import com.laamella.petrolpump.pump.*;
import io.vavr.control.Option;
import nz.sodium.StreamLoop;
import nz.sodium.Unit;

public class ClearSalePump implements Pump {
    public Outputs create(Inputs inputs) {
        StreamLoop<Fuel> sStart = new StreamLoop<>();
        Fill fi = new Fill(
                inputs.sClearSale.map(u -> Unit.UNIT),
                inputs.sFuelPulses, inputs.calibration,
                inputs.price1, inputs.price2, inputs.price3,
                sStart);
        NotifyPointOfSale np = new NotifyPointOfSale(
                new LifeCycle(inputs.sNozzle1,
                        inputs.sNozzle2,
                        inputs.sNozzle3),
                inputs.sClearSale,
                fi);
        sStart.loop(np.sStart);
        return new Outputs()
                .setDelivery(np.fuelFlowing.map(
                        of ->
                                of.equals(Option.some(Fuel.ONE)) ? Delivery.FAST1 :
                                        of.equals(Option.some(Fuel.TWO)) ? Delivery.FAST2 :
                                                of.equals(Option.some(Fuel.THREE)) ? Delivery.FAST3 :
                                                        Delivery.OFF))
                .setSaleCostLCD(fi.dollarsDelivered.map(
                        Formatters::formatSaleCost))
                .setSaleQuantityLCD(fi.litersDelivered.map(
                        Formatters::formatSaleQuantity))
                .setPriceLCD1(ShowDollarsPump.priceLCD(np.fillActive, fi.price,
                        Fuel.ONE, inputs))
                .setPriceLCD2(ShowDollarsPump.priceLCD(np.fillActive, fi.price,
                        Fuel.TWO, inputs))
                .setPriceLCD3(ShowDollarsPump.priceLCD(np.fillActive, fi.price,
                        Fuel.THREE, inputs))
                .setBeep(np.sBeep)
                .setSaleComplete(np.sSaleComplete);
    }
}

