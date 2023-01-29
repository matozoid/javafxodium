package com.laamella.petrolpump.chapter4.section9;

import com.laamella.petrolpump.pump.Formatters;
import com.laamella.petrolpump.pump.Inputs;
import com.laamella.petrolpump.pump.Outputs;
import com.laamella.petrolpump.pump.Pump;
import nz.sodium.Stream;

public class KeypadPump implements Pump {
    public Outputs create(Inputs inputs) {
        Keypad ke = new Keypad(inputs.sKeypad, new Stream<>());
        return new Outputs()
                .setPresetLCD(ke.value.map(v ->
                        Formatters.formatSaleCost((double) v)))
                .setBeep(ke.sBeep);
    }
}

