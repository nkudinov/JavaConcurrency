package com.code.de;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Phaser1 {

    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();

    private int registeredParties = 0;
    private int arrivedParties = 0;
    private int phase = 0;

    public void register() {
        lock.lock();
        try {
            registeredParties++;
        } finally {
            lock.unlock();
        }
    }

    public int getPhase() {
        lock.lock();
        try {
            return phase;
        } finally {
            lock.unlock();
        }
    }

    public void arriveAndAwaitAdvance() throws InterruptedException {
        lock.lock();
        try {
            int currentPhase = phase;
            arrivedParties++;

            if (arrivedParties == registeredParties) {
                phase++;
                arrivedParties = 0;
                condition.signalAll();
            } else {
                while (phase == currentPhase) {
                    condition.await();
                }
            }
        } finally {
            lock.unlock();
        }
    }
}
