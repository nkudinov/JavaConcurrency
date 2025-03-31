package com.code.de;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class ReentrantLock2 {

    private final AtomicInteger state = new AtomicInteger(0);
    private final AtomicReference<Thread> owner = new AtomicReference<>();

    public void lock() {
        Thread current = Thread.currentThread();
        if (owner.get() == current) {

            state.incrementAndGet();
            return;
        }
        while (!state.compareAndSet(0, 1)) {
            Thread.yield();
        }
        owner.set(current);
    }

    public void unlock() {
        Thread current = Thread.currentThread();
        if (owner.get() != current) {
            throw new IllegalStateException("Unlock attempted by a thread that doesn't own the lock");
        }

        int newState = state.decrementAndGet();
        if (newState == 0) {
            owner.set(null);
        }
    }
}
