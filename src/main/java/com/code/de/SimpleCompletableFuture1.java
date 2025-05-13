package com.code.de;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SimpleCompletableFuture1<T> {


    private T item;
    private boolean hasValue;

    private final Lock lock;
    private final Condition condition;

    public SimpleCompletableFuture1() {
        lock = new ReentrantLock();
        hasValue = false;
        condition = lock.newCondition();
    }

    public SimpleCompletableFuture1(Callable<T> callable) {
        this();
        new Thread() {
            @Override
            public void run() {
                try {
                    complete(callable.call());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }.start();
    }

    public static  <V> SimpleCompletableFuture1<V> thanSupply(Callable<V> callable) {
        return new SimpleCompletableFuture1<V>(callable);
    }

    private T obj;

    public void complete(T obj) {
        lock.lock();
        try {
            if (hasValue) {
                throw new IllegalStateException("Future is already completed");
            }
            hasValue = true;
            item = obj;
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public T get() {
        lock.lock();
        try {
            while (!hasValue) {
                condition.await();
            }
            return item;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        var f = SimpleCompletableFuture1.thanSupply(() -> {
            return "hello";
        });
        System.out.println(f.get());
    }
}
