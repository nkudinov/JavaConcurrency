package com.code.de;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CountDownLatch2 {

    private volatile int count;
    private final Lock lock;
    private final Condition condition;

    public CountDownLatch2(int count) {
        this.count = count;
        this.lock = new ReentrantLock();
        this.condition = lock.newCondition(); // FIX: Initialize condition
    }

    public void countDown() { // FIX: Correct method name
        lock.lock();
        try {
            if (count > 0) {
                count--;
                if (count == 0) {
                    condition.signalAll();
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
                condition.await();
            }
        } finally {
            lock.unlock();
        }
    }
}