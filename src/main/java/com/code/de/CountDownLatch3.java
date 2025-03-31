package com.code.de;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CountDownLatch3 {

    private volatile int count;

    Lock lock;
    Condition condition;

    public CountDownLatch3(int count) {
        this.count = count;
        this.lock = new ReentrantLock();
        this.condition = lock.newCondition();
    }

    public void countDown() {
        lock.lock();
        try {
            count--;
            condition.signalAll();
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

    public static void main(String[] args) {

    }
}
