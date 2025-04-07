package com.code.de;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class SimpleReentrantLock {

    private final AtomicReference<Thread> thread = new AtomicReference<>(null);
    private final AtomicInteger state = new AtomicInteger(0);

    public void lock() {
        Thread cur = Thread.currentThread();
        if (cur == thread.get()) {
            state.incrementAndGet(); // Reentrant
            return;
        }

        while (!thread.compareAndSet(null, cur)) {
            Thread.yield();
        }
        state.set(1);
    }

    public void unlock() {
        Thread cur = Thread.currentThread();
        if (thread.get() != cur) {
            throw new IllegalMonitorStateException("Current thread does not hold the lock");
        }

        int val = state.decrementAndGet();
        if (val == 0) {
            thread.set(null);
        }
    }
}