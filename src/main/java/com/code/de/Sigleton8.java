package com.code.de;

public class Sigleton8 {

    private static volatile Sigleton8 instance;

    private Sigleton8() {
    }

    public static Sigleton8 getInstance() {
        if (instance == null) {
            synchronized (Sigleton8.class) {
                if (instance == null) {
                    instance = new Sigleton8();
                }
            }
        }
        return instance;
    }
}
