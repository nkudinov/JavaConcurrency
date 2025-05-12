package com.code.de;

public class Singleton9 {
   private static volatile Singleton9 singleton9;

    private Singleton9() {

    }
    public static Singleton9 getInstance() {
        if (singleton9 == null) {
            synchronized (Singleton9.class) {
                if (singleton9 == null) {
                    singleton9 = new Singleton9();
                }
            }
        }
        return singleton9;
    }
}
