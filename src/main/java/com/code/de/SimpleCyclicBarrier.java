package com.code.de;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SimpleCyclicBarrier {

    private int count;
    private final int initialCount;
    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();

    public SimpleCyclicBarrier(int count) {
        this.count = count;
        this.initialCount = count;
    }

    public void await() throws InterruptedException {
        lock.lock();
        try {
            count--;
            if (count == 0) {
                count = initialCount; // Reset for next round
                condition.signalAll(); // Release all waiting threads
            } else {
                while (count != initialCount) {
                    condition.await(); // Wait until the barrier is tripped
                }
            }
        } finally {
            lock.unlock();
        }
    }
}