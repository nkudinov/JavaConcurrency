package com.code.de;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SimpleBlockingQueue2<T> {


    private volatile Queue<T> queue;
    private Lock lock;

    private Condition notEmpty;
    private Condition notFull;
    private int capacity;

    public SimpleBlockingQueue2(int capacity) {
        this.queue = new LinkedList<>();
        this.capacity = capacity;
        this.lock = new ReentrantLock();
        this.notEmpty = lock.newCondition();
        this.notFull = lock.newCondition();
    }

    public void add(T obj) throws InterruptedException {
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

    public T poll() throws InterruptedException {
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
}
