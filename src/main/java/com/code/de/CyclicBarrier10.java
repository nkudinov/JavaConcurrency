package com.code.de;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CyclicBarrier10 {

    private final int parties;
    private int count;
    private int generation = 0;

    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();

    public CyclicBarrier10(int parties) {
        if (parties <= 0) {
            throw new IllegalArgumentException("Barrier parties must be > 0");
        }
        this.parties = parties;
        this.count = parties;
    }

    public void await() throws InterruptedException {
        lock.lock();
        try {
            int currentGeneration = generation;
            count--;

            if (count == 0) {

                generation++;
                count = parties;
                condition.signalAll();
                return;
            }

            while (generation == currentGeneration) {
                condition.await();
            }
        } finally {
            lock.unlock();
        }
    }
}
