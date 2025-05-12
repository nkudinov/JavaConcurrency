package com.code.de;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SimpleCountDownLatch {

    private int count;
    private final Lock lock = new ReentrantLock();
    private final Condition zero = lock.newCondition();

    public SimpleCountDownLatch(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Count cannot be negative");
        }
        this.count = count;
    }

    public void countDown() {
        lock.lock();
        try {
            if (count > 0) {
                count--;
                if (count == 0) {
                    zero.signalAll(); // Wake up all waiting threads
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public void await() throws InterruptedException {
        lock.lock();
        try {
            while (count != 0) {
                zero.await();
            }
        } finally {
            lock.unlock();
        }
    }
}