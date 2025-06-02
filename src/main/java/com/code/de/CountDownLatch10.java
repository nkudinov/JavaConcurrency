package com.code.de;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CountDownLatch10 {

    private int count;
    private Lock lock;
    private Condition notZero;

    public CountDownLatch10(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("should be not negative");
        }
        this.count = count;
        lock = new ReentrantLock();
        notZero = lock.newCondition();
    }

    public void countDown() {
        lock.lock();
        try {
            count--;
            if (count == 0) {
                notZero.signalAll();
            }
        } finally {
            lock.unlock();
        }
    }

    public void await() throws InterruptedException {
        lock.lock();
        try {
            while (count != 0) {
                notZero.await();
            }
        } finally {
            lock.unlock();
        }
    }

}
