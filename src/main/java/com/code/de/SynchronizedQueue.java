package com.code.de;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SynchronizedQueue<T> {

    private T item = null;
    private boolean hasItem = false;

    private final Lock lock = new ReentrantLock();
    private final Condition itemAvailable = lock.newCondition();
    private final Condition slotAvailable = lock.newCondition();

    public void put(T newItem) throws InterruptedException {
        if (newItem == null) throw new NullPointerException();

        lock.lock();
        try {
            while (hasItem) {
                slotAvailable.await(); // Wait for the slot to become available
            }
            item = newItem;
            hasItem = true;
            itemAvailable.signal();
            while (hasItem) {
                slotAvailable.await();
            }
        } finally {
            lock.unlock();
        }
    }

    public T take() throws InterruptedException {
        lock.lock();
        try {
            while (!hasItem) {
                itemAvailable.await();
            }
            T result = item;
            item = null;
            hasItem = false;
            slotAvailable.signal();
            return result;
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        SynchronousQueue<String> queue = new SynchronousQueue<>();

        Thread producer = new Thread(() -> {
            try {
                System.out.println("Putting: hello");
                queue.put("hello");
                System.out.println("Put done");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread consumer = new Thread(() -> {
            try {
                Thread.sleep(1000); // Ensure producer blocks
                String value = queue.take();
                System.out.println("Took: " + value);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        producer.start();
        consumer.start();
    }
}
