package com.code.de;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class ReentrantLock3 {

    private AtomicReference<Thread> thread = new AtomicReference<>(null);
    private AtomicInteger state = new AtomicInteger(0);

    public void lock() {
        Thread cur = Thread.currentThread();

        if (thread.get() == cur) {
            state.incrementAndGet();
            return;
        }

        while (!thread.compareAndSet(null, cur)) {
        }

        state.incrementAndGet();
    }

    public void unlock() {
        Thread cur = Thread.currentThread();

        if (thread.get() != cur) {
            throw new IllegalStateException("Current thread does not hold the lock");
        }

        int val = state.decrementAndGet();

        if (val == 0) {
            thread.set(null);
        }
    }
}
