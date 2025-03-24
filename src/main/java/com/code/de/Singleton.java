package com.code.de;

public class Singleton {

    private Singleton() {
    }


    public static Singleton getInstance() {
        return Holder.singleton;
    }

    static class Holder {

        public static Singleton singleton = new Singleton();
    }

}
