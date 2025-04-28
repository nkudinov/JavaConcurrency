package com.code.de;

import java.util.Collections;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CyclicBarrier7 {

    private final int permits;
    private final Lock lock;
    private final Condition condition;
    private int generation;
    private int count;

    public CyclicBarrier7(int permits) {
        if (permits <= 0) {
            throw new IllegalArgumentException("permits must be greater than 0");
        }
        this.permits = permits;
        this.lock = new ReentrantLock();
        this.condition = lock.newCondition();
        this.generation = 0;
        this.count = 0;
    }

    public void await() throws InterruptedException {
        lock.lock();
        try {
            int curGeneration = generation;
            count++;
            if (count == permits) {
                count = 0;
                generation++;
                condition.signalAll();
            } else {
                while (generation == curGeneration) {
                    condition.await();
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {

    }

}
