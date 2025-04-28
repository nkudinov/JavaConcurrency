package com.code.de;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Semaphore7 {

    private final int permits;
    private int count;
    private Lock lock;
    private Condition notEnought;

    public Semaphore7(int permits) {
        this.permits = permits;
        this.lock = new ReentrantLock();
        this.notEnought = lock.newCondition();
    }

    public void acquire() {
        lock.lock();
        try {
            while (count > permits) {
                notEnought.await();
            }
            count++;
        } finally {
            lock.unlock();
        }
    }

    public void release() {
        lock.lock();
        try {
            count--;
            notEnought.signalAll();
        } finally {
            lock.unlock();
        }
    }
}
