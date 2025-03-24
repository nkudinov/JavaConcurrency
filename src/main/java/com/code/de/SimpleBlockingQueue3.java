package com.code.de;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SimpleBlockingQueue<T> {

    private final Lock lock = new ReentrantLock();
    private final int capacity;
    private final Condition notEmpty = lock.newCondition();
    private final Condition notFull = lock.newCondition();
    private final Queue<T> queue = new LinkedList<>();

    public SimpleBlockingQueue(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than zero");
        }
        this.capacity = capacity;
    }

    public void put(T obj) throws InterruptedException {
        lock.lock();
        try {
            while (queue.size() == capacity) {
                notFull.await();
            }
            queue.add(obj);
            notEmpty.signal();  // Use signal() instead of signalAll() for performance
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
            T obj = queue.poll();
            notFull.signal();
            return obj;
        } finally {
            lock.unlock();
        }
    }

    public T peek() {
        lock.lock();
        try {
            return queue.peek();
        } finally {
            lock.unlock();
        }
    }

    public int size() {
        lock.lock();
        try {
            return queue.size();
        } finally {
            lock.unlock();
        }
    }

    public boolean isEmpty() {
        lock.lock();
        try {
            return queue.isEmpty();
        } finally {
            lock.unlock();
        }
    }
}