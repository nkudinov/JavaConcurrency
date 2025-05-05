package com.code.de;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CyclicBarrier8 {

    private int permits;
    private final int maxPermits;
    private int generation;

    private final Lock lock;
    private final Condition condition;

    public CyclicBarrier8(int permits) {
        this.permits = permits;
        this.maxPermits = permits;
        this.generation = 0;
        this.lock = new ReentrantLock();
        this.condition = lock.newCondition();
    }

    public void await() throws InterruptedException {
        lock.lock();
        try {
            int cur = generation;
            permits--;
            if (permits == 0) {
                generation++;
                permits = maxPermits;
                condition.signalAll();
            } else {
                while (cur == generation) {
                    condition.await();
                }
            }
        } finally {
            lock.unlock();
        }
    }
}
