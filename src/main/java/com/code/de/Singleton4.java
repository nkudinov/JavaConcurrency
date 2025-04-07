package com.code.de;

public class Singleton4 {

    private static volatile Singleton4 instance;


    private Singleton4() {

    }

    public Singleton4 getInstance() {
        if (instance == null) {
            synchronized (this) {
                if (instance == null) {
                    instance = new Singleton4();
                }
            }
        }
        return instance;
    }
}
