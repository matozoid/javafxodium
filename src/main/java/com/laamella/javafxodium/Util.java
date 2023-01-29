package com.laamella.javafxodium;

import javafx.application.Platform;

import static java.util.Objects.requireNonNull;
import static javafx.application.Platform.*;

public class Util {
    /**
     * This method is used to run a specified Runnable in the FX Application thread,
     * it returns before the task finished execution
     *
     * @param doRun This is the sepcifed task to be excuted by the FX Application thread
     */
    public static void runInFX(Runnable doRun) {
        if (isFxApplicationThread()) {
            doRun.run();
        } else {
            runLater(doRun);
        }
    }

    public static String classpathUrl(String resourceName) {
        return requireNonNull(Util.class.getResource(resourceName)).toExternalForm();
    }

}
