package com.code.de;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SimpleBlockingQueue4<T> {

    private final int capacity;
    Queue<T> queue;
    Lock lock;
    Condition notFull;
    Condition notEmpty;

    public SimpleBlockingQueue4(int capacity) {
        this.capacity = capacity;
        this.queue = new LinkedList<>();
        this.lock = new ReentrantLock();
        this.notFull = lock.newCondition();
        this.notEmpty = lock.newCondition();
    }

    public void put(T obj) throws InterruptedException {
        lock.lock();
        try {
            while (queue.size() == capacity) {
                notFull.await();
            }
            queue.add(obj);
            notEmpty.signalAll();
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
             notFull.signalAll();
             return obj;
         } finally {
             lock.unlock();
         }
    }
}
