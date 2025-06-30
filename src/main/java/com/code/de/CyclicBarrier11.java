package com.code.de;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CyclicBarrier11 {

    protected final int parties;
    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private int count;
    private int clock;

    public CyclicBarrier11(int parties) {
        this.parties = parties;
        this.count = 0;
        this.clock = 0;
    }

    public void await() throws InterruptedException {
        lock.lock();
        try {
            int currentGeneration = clock;

            count++;
            if (count == parties) {
                clock++;     // Start a new generation
                count = 0;        // Reset for reuse
                condition.signalAll();
            } else {
                while (currentGeneration == this.clock) {
                    condition.await();
                }
            }
        } finally {
            lock.unlock();
        }
    }
}
