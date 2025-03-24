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
            // Decrement the count
            count--;

            // If the current thread is the last one to arrive
            if (count == 0) {
                // Notify all waiting threads that they can continue
                condition.signalAll();
                // Reset the count for the next use of the barrier
                count = initialCount;
            } else {
                // If not the last one, wait until the count reaches zero
                condition.await();
            }
        } finally {
            lock.unlock();
        }
    }
}
