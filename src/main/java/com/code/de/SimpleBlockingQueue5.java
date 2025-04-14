package com.code.de;

import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SimpleBlockingQueue5<T> {

    private final int capacity;
    private final Lock lock;
    private final Condition notFull;
    private final Condition notEmpty;

    private final Queue<T> queue;

    public SimpleBlockingQueue5(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity of queue could not be negative or zero");
        }
        this.capacity = capacity;
        this.queue = new LinkedList<>();
        this.lock = new ReentrantLock();
        this.notEmpty = lock.newCondition();
        this.notFull = lock.newCondition();
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
            return res;
        } finally {
            lock.unlock();
        }
    }
}
