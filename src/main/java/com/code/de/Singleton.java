package com.code.de;

public class Singleton {

    private static volatile Singleton singleton;

    private Singleton() {
        // Prevent instantiation
    }

    public static Singleton getInstance() {
        if (singleton == null) {
            synchronized (Singleton.class) {
                if (singleton == null) {
                    singleton = new Singleton();
                }
            }
        }
        return singleton;
    }
}
