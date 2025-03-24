package com.code.de;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CountDownLatch {

    private volatile int count;
    private Lock lock;
    private Condition condition;

    public CountDownLatch(int count) {
        this.count = count;
        lock = new ReentrantLock();
        condition = lock.newCondition();
    }

    public void countDown() {
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
            while (count > 0) {
                condition.await();
            }
        } finally {
            lock.unlock();
        }
    }
}
