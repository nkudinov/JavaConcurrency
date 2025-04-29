package com.code.de;

import java.util.concurrent.Callable;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SimpleCompletableFuture<T> {

    private T obj;
    private boolean isCompleted;
    private final Lock lock;
    private final Condition done;

    public SimpleCompletableFuture() {
        this.lock = new ReentrantLock();
        this.isCompleted = false;
        this.done = lock.newCondition();
    }

    public T get() throws InterruptedException {
        lock.lock();
        try {
            while (!isCompleted) {
                done.await();
            }
            return obj;
        } finally {
            lock.unlock();
        }
    }

    public void complete(T obj) {

        lock.lock();
        try {
            isCompleted = true;
            this.obj = obj;
            done.signalAll();
        } finally {
            lock.unlock();
        }
    }

    static public <R> SimpleCompletableFuture<R> supplyAsync(Callable<R> callable) {
        SimpleCompletableFuture<R> res = new SimpleCompletableFuture<>();
        Thread worker = new Thread() {
            @Override
            public void run() {
                try {
                    res.complete(callable.call());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
        worker.start();
        return res;
    }

    public static void main(String[] args) throws InterruptedException {
        SimpleCompletableFuture<String> test = SimpleCompletableFuture.supplyAsync(() -> {
            return "hello";
        });
        System.out.println(test.get());
    }

}
