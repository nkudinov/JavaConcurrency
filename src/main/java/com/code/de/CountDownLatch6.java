package com.code.de;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CountDownLatch6 {

    private int count;
    private Lock lock;
    private Condition notZero;

    public CountDownLatch6(int count) {
        this.count = count;
        this.lock = new ReentrantLock();
        this.notZero = lock.newCondition();
    }

    public void countDown() {
        lock.lock();
        try {
            if (count > 0) {
                count--;
                if (count == 0) {
                    notZero.signalAll();
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
                notZero.await();
            }
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        CountDownLatch c = new CountDownLatch(1);
    }
}
