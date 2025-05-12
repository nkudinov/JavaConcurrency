package com.code.de;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Semaphore9 {

    private int permits;
    private final int maxPermits;
    private final Lock lock;
    private final Condition condition;

    public Semaphore9(int initialPermits, int maxPermits, boolean fair) {
        if (initialPermits < 0 || initialPermits > maxPermits) {
            throw new IllegalArgumentException("Initial permits must be between 0 and maxPermits");
        }
        this.permits = initialPermits;
        this.maxPermits = maxPermits;
        this.lock = new ReentrantLock(fair);
        this.condition = lock.newCondition();
    }

    public void acquire() throws InterruptedException {
        lock.lock();
        try {
            while (permits == 0) {
                condition.await();
            }
            permits--;
        } finally {
            lock.unlock();
        }
    }

    public boolean tryAcquire() {
        lock.lock();
        try {
            if (permits > 0) {
                permits--;
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    public boolean tryAcquire(long timeout, TimeUnit unit) throws InterruptedException {
        long nanos = unit.toNanos(timeout);
        lock.lock();
        try {
            while (permits == 0) {
                if (nanos <= 0L) {
                    return false;
                }
                nanos = condition.awaitNanos(nanos);
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
            if (permits < maxPermits) {
                permits++;
                condition.signal();
            }
        } finally {
            lock.unlock();
        }
    }

    public int availablePermits() {
        lock.lock();
        try {
            return permits;
        } finally {
            lock.unlock();
        }
    }
}
