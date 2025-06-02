package com.code.de;

public class Singleton10 {
   private static volatile Singleton10 singleton10;

    private Singleton10() {
    }

    public static Singleton10  getInstance(){
       if (singleton10==null) {
           synchronized (Singleton10.class) {
               if (singleton10 == null) {
                   singleton10 =  new Singleton10();
               }
           }
       }
       return singleton10;
   }
}
