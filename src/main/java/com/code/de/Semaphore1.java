package com.code.de;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Semaphore1 {

    private final Lock lock;
    private final Condition moreThanZero;

    public Semaphore1(int permits) {
        if (permits <= 0) {
            throw new IllegalArgumentException(" For simplicity. Number of permit could not be negative")
        }
        this.permits = permits;
        this.lock = new ReentrantLock();
        this.moreThanZero = lock.newCondition();
    }

    private int permits;

    public void acquire() throws InterruptedException {
        lock.lock();
        try {
            while (permits == 0) {
               moreThanZero.await();
            }
            permits--;

        } finally {
            lock.unlock();
        }
    }

    public void release() {
        lock.lock();
        try {
            permits++;
            moreThanZero.signal();
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {

    }
}
