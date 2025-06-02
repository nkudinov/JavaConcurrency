package com.code.de;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SimpleBlockingQueue10<V> {

    private final int capacity;

    private final Lock lock;
    private final Condition notEmpty;
    private final Condition notFull;

    private final Queue<V> queue;

    public SimpleBlockingQueue10(int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("capacity could not be negative");
        }
        this.capacity = capacity;
        lock = new ReentrantLock();
        notFull = lock.newCondition();
        notEmpty = lock.newCondition();
        queue = new LinkedList<>();
    }

    public void put(V cal) throws InterruptedException {
        lock.lock();
        try {
            while (queue.size() == capacity) {
                notFull.await();
            }
            queue.add(cal);
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    public V take() throws InterruptedException {
        lock.lock();
        try {
            while (queue.isEmpty()) {
                notEmpty.await();
            }
            V res = queue.poll();
            notFull.signal();
            return res;
        } finally {
            lock.unlock();
        }
    }
}
