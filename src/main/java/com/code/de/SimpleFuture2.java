package com.code.de;

public class SimpleFuture2 {

    public static void main(String[] args) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
            }
        };
        thread.interrupt();
    }

}
