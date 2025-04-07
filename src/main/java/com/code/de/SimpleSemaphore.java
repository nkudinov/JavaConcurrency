package com.code.de;

public class SimpleSemaphore {

    private volatile int permits;

    public SimpleSemaphore(int permits) {
        this.permits = permits;
    }
    public synchronized void aquire() throws InterruptedException {
        while (permits == 0) {
            wait();
        }
        permits--;
    }
    public synchronized void release(){
        permits++;
        notifyAll();
    }
}
