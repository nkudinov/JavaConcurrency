package com.code.de;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SimpleBlockingQueue9<T> {

    private final int capacity;
    private Lock lock;
    private final Condition notEmpty;
    private final Condition notFull;

    private Queue<T> queue;

    public SimpleBlockingQueue9(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("queue capacity could not be negative");
        }
        this.capacity = capacity;
        this.lock = new ReentrantLock();
        this.notEmpty = lock.newCondition();
        this.notFull = lock.newCondition();
        this.queue = new LinkedList<>();
    }

    public void put(T obj) throws InterruptedException {
        lock.lock();
        try {
             while (queue.size() == capacity) {
                 notFull.await();
             }
             queue.add(obj);
             notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    public T take() throws InterruptedException {
        lock.lock();
        try {
            while (queue.isEmpty()) {
                notEmpty.await();
            }
            T res = queue.poll();
            notFull.signal();
            return  res;
        } finally {
            lock.unlock();
        }
    }
}
