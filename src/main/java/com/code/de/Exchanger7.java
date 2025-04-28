package com.code.de;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Exchanger7<T> {

    private boolean hasValue;
    private T firstValue;
    private final Lock lock;
    private final Condition condition;

    public Exchanger7() {
        this.lock = new ReentrantLock();
        this.condition = lock.newCondition();
        this.hasValue = false;
    }

    public T exchange(T obj) throws InterruptedException {
        lock.lock();
        try {
            if (!hasValue) {
                // Первый поток зашел
                hasValue = true;
                firstValue = obj;
                while (hasValue) {
                    condition.await();
                }
                T res = firstValue;
                firstValue = null; // очистим ссылку
                return res;
            } else {
                // Второй поток зашел
                T res = firstValue;
                firstValue = obj;
                hasValue = false;
                condition.signal();
                return res;
            }
        } finally {
            lock.unlock();
        }
    }
}
