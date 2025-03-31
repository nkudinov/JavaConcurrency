package com.code.de;

import java.util.concurrent.Callable;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SimpleFuture<T> {

    private T result;
    private boolean isDone = false;
    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private Thread workerThread;

    public SimpleFuture(Callable<T> task) {
        new Thread(() -> {
            try {
                T value = task.call();
                lock.lock();
                try {
                    result = value;
                    isDone = true;
                    condition.signalAll(); // Notify waiting threads
                } finally {
                    lock.unlock();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                workerThread = null; // Allow garbage collection
            }
        }).start();

    }

    public T get() throws InterruptedException {
        lock.lock();
        try {
            while (!isDone) {
                condition.await(); // Efficient waiting
            }
            return result;
        } finally {
            lock.unlock();
        }
    }

    public boolean isDone() {
        lock.lock();
        try {
            return isDone;
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        SimpleFuture<String> stringSimpleFuture = new SimpleFuture<>(() -> {
            return "hello";
        });
        System.out.println(stringSimpleFuture.get());
    }
}
