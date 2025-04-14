package com.code.de;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CyclicBarrier3 {

    private final int permits;
    private int count;
    private int generation = 0;

    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();

    public CyclicBarrier3(int permits) {
        if (permits <= 0) {
            throw new IllegalArgumentException("Permits must be > 0");
        }
        this.permits = permits;
        this.count = permits;
    }

    public void await() throws InterruptedException {
        lock.lock();
        try {
            int currentGeneration = generation;

            count--;

            if (count == 0) {
                generation++;
                count = permits;
                condition.signalAll();
            } else {
                while (currentGeneration == generation) {
                    condition.await();
                }
            }

        } finally {
            lock.unlock();
        }
    }
}
