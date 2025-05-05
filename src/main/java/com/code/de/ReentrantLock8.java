package com.code.de;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLock8 {

    private final AtomicReference<Thread> owner = new AtomicReference<>(null);
    private final AtomicInteger holdCount = new AtomicInteger(0);

    public void lock() {
        Thread current = Thread.currentThread();

        // Reentrant acquisition
        if (owner.get() == current) {
            holdCount.incrementAndGet();
            return;
        }

        // Spin until lock is acquired
        while (!owner.compareAndSet(null, current)) {
            Thread.yield(); // Poor man's backoff, real impls use better blocking
        }

        // First acquisition
        holdCount.set(1);
    }

    public void unlock() {
        Thread current = Thread.currentThread();

        if (owner.get() != current) {
            throw new IllegalMonitorStateException("Calling thread has not locked this lock");
        }

        int remaining = holdCount.decrementAndGet();

        if (remaining == 0) {
            owner.set(null);
        }
    }

    public boolean isHeldByCurrentThread() {
        return owner.get() == Thread.currentThread();
    }

    public int getHoldCount() {
        return isHeldByCurrentThread() ? holdCount.get() : 0;
    }
}
