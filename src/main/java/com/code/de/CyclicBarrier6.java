package com.code.de;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CyclicBarrier6 {

    private final int parties;
    private int count;
    private int generation;

    private final Lock lock = new ReentrantLock();
    private final Condition trip = lock.newCondition();

    public CyclicBarrier6(int parties) {
        if (parties <= 0) {
            throw new IllegalArgumentException("parties must be > 0");
        }
        this.parties = parties;
        this.count = parties;
        this.generation = 0;
    }

    public void await() throws InterruptedException {
        lock.lock();
        try {
            int currentGen = generation;

            count--;
            if (count == 0) {
                generation++;
                count = parties;
                trip.signalAll();
                return;
            }

            while (generation == currentGen) {
                trip.await();
            }

        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) throws BrokenBarrierException, InterruptedException {
        CyclicBarrier c = new CyclicBarrier(1);
        c.await();
    }
}
