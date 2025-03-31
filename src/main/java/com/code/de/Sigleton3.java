package com.code.de;



    public class Singleton3 {
        // Use volatile to ensure proper synchronization of the instance in a multi-threaded environment
        private static volatile Singleton3 instance;

        // Private constructor to prevent instantiation from outside
        private Singleton3() {}

        public static Singleton3 getInstance() {
            if (instance == null) {
                synchronized (Singleton3.class) {
                    // Double-checked locking to ensure only one instance is created
                    if (instance == null) {
                        instance = new Singleton3();
                    }
                }
            }
            return instance;
        }
    }

