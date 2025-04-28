package com.code.de;


public class Sigleton7 {


    private static class Holder {

        private static final Sigleton7 INSTANCE = new Sigleton7();
    }


    public static Sigleton7 getInstance() {
        return Holder.INSTANCE;
    }


    private Sigleton7() {
    }
}