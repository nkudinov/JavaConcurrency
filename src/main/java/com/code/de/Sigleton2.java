package com.code.de;

public class Sigleton2 {

    private Sigleton2() {

    }

    private volatile Sigleton2 sigleton2;

    public Sigleton2 getInstance() {
        if (sigleton2 == null) {
            synchronized (Sigleton2.class) {
                if (sigleton2 == null) {
                    sigleton2 = new Sigleton2();
                }
            }
        }
        return sigleton2;
    }
}
