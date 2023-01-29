package com.laamella.javafxodium;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.Parent;
import nz.sodium.Listener;

import java.util.function.Supplier;

public class ListenerManager implements ChangeListener<Parent> {
    private final Supplier<Listener> listenerCreator;
    private Listener l;

    private ListenerManager(Supplier<Listener> listenerCreator) {
        this.listenerCreator = listenerCreator;
    }

    @Override
    public void changed(ObservableValue<? extends Parent> observable, Parent oldValue, Parent newValue) {
        if (l != null) {
            l.unlisten();
            l = null;
        }
        if (newValue != null) {
            l = listenerCreator.get();
        }
    }
    
    public static void addManagedListener(Node node, Supplier<Listener> listenerCreator){
        node.parentProperty().addListener(new ListenerManager(listenerCreator));
    }
}
