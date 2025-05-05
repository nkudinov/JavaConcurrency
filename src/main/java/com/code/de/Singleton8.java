package com.code.de;

public class Singleton8 {

    private static class HOLDER {

        private static final Singleton8 instance = new Singleton8();
    }

    private Singleton8() {
        if (HOLDER.instance != null) {
            throw new IllegalStateException("Already instantiated");
        }
    }

    public static Singleton8 getInstance() {
        return HOLDER.instance;
    }
}
