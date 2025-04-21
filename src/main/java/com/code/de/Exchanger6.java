package com.code.de;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Exchanger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Exchanger6<T> {

    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();

    private boolean hasItem = false;
    private T firstItem;

    public T exchange(T myItem) throws InterruptedException {
        lock.lock();
        try {
            if (!hasItem) {

                hasItem = true;
                firstItem = myItem;
                while (hasItem) {
                    condition.await();
                }
                T otherItem = firstItem;
                firstItem = null;
                return otherItem;
            } else {
                T otherItem = firstItem;
                firstItem = myItem;
                hasItem = false;
                condition.signal();
                return otherItem;
            }
        } finally {
            lock.unlock();
        }
    }
    public static void main(String[] args) {
        Exchanger<String> e = new Exchanger<>();
    }
}
