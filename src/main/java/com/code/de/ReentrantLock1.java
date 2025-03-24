package com.code.de;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class ReentrantLock1 {


    private AtomicInteger state; // To track the lock's state
    private AtomicReference<Thread> holder; // To track the thread holding the lock

    public ReentrantLock1() {
        this.state = new AtomicInteger(0);  // 0 means unlocked, 1 means locked
        this.holder = new AtomicReference<>(null); // No thread is holding the lock initially
    }

    public void lock() {
        Thread currentThread = Thread.currentThread();

        // Check if the current thread already holds the lock (reentrant)
        if (currentThread == holder.get()) {
            state.incrementAndGet();  // If the same thread, increment the state (reentrancy)
            return;
        }

        // If the lock is not held by anyone, try to acquire it
        while (!state.compareAndSet(0, 1)) {
            // Busy-wait until we successfully acquire the lock
            // You could add a small sleep here to prevent tight spinning if needed
        }

        // Lock acquired by this thread, set the holder to this thread
        holder.set(currentThread);
    }

    public void unlock() {
        Thread currentThread = Thread.currentThread();

        // Check if the current thread holds the lock
        if (currentThread != holder.get()) {
            throw new IllegalMonitorStateException("Current thread does not hold the lock");
        }

        // Decrease the state (reentrancy check)
        if (state.decrementAndGet() == 0) {
            // If the state is 0, the lock is released, set holder to null
            holder.set(null);
        }
    }
}
