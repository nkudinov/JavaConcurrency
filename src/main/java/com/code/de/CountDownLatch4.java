package com.code.de;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CountDownLatch4 {

    private int permits;
    private final Lock lock = new ReentrantLock();
    private final Condition notZero = lock.newCondition();

    public CountDownLatch4(int permits) {
        if (permits <= 0) {
            throw new IllegalArgumentException("Permits should be positive");
        }
        this.permits = permits;
    }

    public void await() throws InterruptedException {
        lock.lock();
        try {
            while (permits != 0) {
                notZero.await();
            }
        } finally {
            lock.unlock();
        }
    }

    public void countDown() {
        lock.lock();
        try {
            if (permits > 0) {
                permits--;
                if (permits == 0) {
                    notZero.signalAll();
                }
            }
        } finally {
            lock.unlock();
        }
    }
}
