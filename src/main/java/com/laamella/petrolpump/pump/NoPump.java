package com.laamella.petrolpump.pump;

public class NoPump implements Pump {
    @Override
    public Outputs create(Inputs inputs) {
        return new Outputs();
    }
}
