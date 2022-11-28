package me.coconan.agileaccount;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Executor {
    private static final ExecutorService network = Executors.newCachedThreadPool();

    public static ExecutorService networkIO() {
        return network;
    }
}
