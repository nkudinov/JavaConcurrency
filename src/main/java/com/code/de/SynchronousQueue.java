package com.code.de;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SynchronousQueue<T> {

    private T item = null;
    private boolean hasItem = false;

    private final Lock lock = new ReentrantLock();
    private final Condition itemAvailable = lock.newCondition();
    private final Condition itemTaken = lock.newCondition();

    public void put(T value) throws InterruptedException {
        lock.lock();
        try {
            while (hasItem) {
                itemTaken.await();
            }
            item = value;
            hasItem = false;

        } finally {
            lock.unlock();
        }
    }


}
