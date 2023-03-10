package com.laamella.petrolpump.chapter4.section11;

import com.laamella.petrolpump.chapter4.section7.Fill;
import com.laamella.petrolpump.pump.Delivery;
import com.laamella.petrolpump.pump.Fuel;
import io.vavr.control.Option;
import nz.sodium.Cell;

public class Preset {
    public final Cell<Delivery> delivery;
    public final Cell<Boolean> keypadActive;

    public enum Speed {FAST, SLOW, STOPPED}

    public Preset(Cell<Integer> presetDollars,
                  Fill fi,
                  Cell<Option<Fuel>> fuelFlowing,
                  Cell<Boolean> fillActive) {
        Cell<Speed> speed = presetDollars.lift(
                fi.price, fi.dollarsDelivered, fi.litersDelivered,
                (presetDollars_, price, dollarsDelivered, litersDelivered) -> {
                    if (presetDollars_ == 0)
                        return Speed.FAST;
                    else {
                        if (dollarsDelivered >= (double) presetDollars_)
                            return Speed.STOPPED;
                        double slowLiters =
                                (double) presetDollars_ / price - 0.10;
                        if (litersDelivered >= slowLiters)
                            return Speed.SLOW;
                        else
                            return Speed.FAST;
                    }
                });
        delivery = fuelFlowing.lift(speed,
                (of, speed_) ->
                        speed_ == Speed.FAST ? (
                                of.equals(Option.some(Fuel.ONE)) ? Delivery.FAST1 :
                                        of.equals(Option.some(Fuel.TWO)) ? Delivery.FAST2 :
                                                of.equals(Option.some(Fuel.THREE)) ? Delivery.FAST3 :
                                                        Delivery.OFF
                        ) :
                                speed_ == Speed.SLOW ? (
                                        of.equals(Option.some(Fuel.ONE)) ? Delivery.SLOW1 :
                                                of.equals(Option.some(Fuel.TWO)) ? Delivery.SLOW2 :
                                                        of.equals(Option.some(Fuel.THREE)) ? Delivery.SLOW3 :
                                                                Delivery.OFF
                                ) :
                                        Delivery.OFF);
        keypadActive = fuelFlowing.lift(speed,
                (of, speed_) ->
                        !of.isDefined() || speed_ == Speed.FAST);
    }
}
