package com.code.de;

public class Singleton6 {

    private static volatile Singleton6 singleton;

    private Singleton6() {

    }
    public static Singleton6 getInstance() {
        if (singleton == null) {
            synchronized (Singleton6.class) {
                if (singleton == null) {
                    singleton = new Singleton6();
                }
            }
        }
        return singleton;
    }
}
