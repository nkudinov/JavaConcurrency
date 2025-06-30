package com.code.de;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CountDownLatch11 {

    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private int count;

    public CountDownLatch11(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("count <= 0");
        }
        this.count = count;
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
            while (this.count > 0) {
                condition.await();
            }
        } finally {
            lock.unlock();
        }
    }

    public int getCount() {
        lock.lock();
        try {
            return count;
        } finally {
            lock.unlock();
        }
    }
}
