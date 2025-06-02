package com.code.de;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Semaphore10 {

    private int permits;
    private Lock lock;
    private Condition positive;

    public Semaphore10(int permits) {
        if (permits < 0) {
            throw new IllegalArgumentException("permits should be positive");
        }
        this.permits = permits;
        lock = new ReentrantLock();
        positive = lock.newCondition();
    }

    public void acquire() throws InterruptedException {
        lock.lock();
        try {
            while (permits <= 0) {
                positive.await();
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
            positive.signal();
        } finally {
            lock.unlock();
        }
    }

}
