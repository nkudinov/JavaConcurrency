package com.code.de;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SimpleBlockingQueue<T> {

    Lock lock;
    Condition notFull;
    Condition notEmpty;

    Queue<T> q;

    public SimpleBlockingQueue() {
        lock = new ReentrantLock();
        notFull = lock.newCondition();
        notEmpty = lock.newCondition();
        q = new LinkedList<>();
    }

    public void add(T obj) throws InterruptedException {
        lock.lock();
        try {

            while (q.size() == 10) {
                notFull.await();
            }
            q.add(obj);
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    public T poll() throws InterruptedException {
        lock.lock();
        try {
            while (q.isEmpty()) {
                notEmpty.await();
            }
            T obj = q.poll();
            notFull.signal();
            return obj;
        } finally {
            lock.unlock();
        }
    }
}
