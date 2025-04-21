package com.code.de;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Semaphore6 {

    private int permits;
    private Lock lock;
    private Condition nonZero;

    public Semaphore6(int permits) {
        this.permits = permits;
        this.lock = new ReentrantLock();
        this.nonZero = lock.newCondition();
    }

    public void acquire() throws InterruptedException {
        lock.lock();
        try {
            while (permits <= 0) {
                nonZero.await();
            }
            permits--;
        } finally {
            lock.unlock();
        }
    }
    public boolean tryAcquire() throws InterruptedException {
        lock.lock();
        try {
            if (permits <= 0) {
               return false;
            }
            permits--;
            return true;
        } finally {
            lock.unlock();
        }
    }
    public void release() {
        lock.lock();
        try {
            permits++;
            nonZero.signal();
        } finally {
            lock.unlock();
        }
    }

}
