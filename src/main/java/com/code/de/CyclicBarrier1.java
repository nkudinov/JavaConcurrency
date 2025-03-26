package com.code.de;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CyclicBarrier1 {

    private int count;
    private final Lock lock;
    private final Condition condition;
    private final int initialCount;

    public CyclicBarrier1(int count) {
        this.count = count;
        this.initialCount = count;
        this.lock = new ReentrantLock();
        this.condition = lock.newCondition();
    }

    public void await() throws InterruptedException {
        lock.lock();
        try {
            count--;
            if (count == 0) {
                condition.signalAll();
                count = initialCount;
            } else {
                condition.await();
            }
        } finally {
            lock.unlock();
        }
    }
}
