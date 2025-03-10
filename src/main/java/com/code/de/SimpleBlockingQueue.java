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

    int capacity;

    public SimpleBlockingQueue(int capacity) {
        this.capacity = capacity;
        q = new LinkedList<>();
        lock = new ReentrantLock();
        notFull = lock.newCondition();
        notEmpty = lock.newCondition();
    }

    public void put(T obj) throws InterruptedException {
        lock.lock();
        try {
            while (q.size() == capacity) {
                notFull.await();
            }
            q.add(obj);
            notEmpty.signal();  // Signal that the queue is no longer empty
        } finally {
            lock.unlock();
        }
    }

    public T get() throws InterruptedException {
        lock.lock();
        try {
            while (q.isEmpty()) {
                notEmpty.await();
            }
            T obj = q.poll();
            notFull.signal();  // Signal that the queue is no longer full
            return obj;
        } finally {
            lock.unlock();
        }
    }
}
