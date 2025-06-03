package com.code.de;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SimpleCompletableFuture2<V> {

    private boolean hasValue;
    private V val;
    private Exception exception;
    private final Lock lock = new ReentrantLock();
    private final Condition notReady = lock.newCondition();

    public SimpleCompletableFuture2() {
    }

    public SimpleCompletableFuture2(V val) {
        this.val = val;
        this.hasValue = true;
    }

    public static <T> SimpleCompletableFuture2<T> supplyAsync(Callable<T> callable) {
        SimpleCompletableFuture2<T> res = new SimpleCompletableFuture2<>();
        new Thread(() -> {
            try {
                T obj = callable.call();
                res.complete(obj);
            } catch (Exception e) {
                res.completeExceptionally(e);
            }
        }).start();
        return res;
    }

    public void complete(V val) {
        lock.lock();
        try {
            if (hasValue || exception != null) {
                throw new IllegalStateException("Already completed");
            }
            this.val = val;
            this.hasValue = true;
            notReady.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void completeExceptionally(Exception e) {
        lock.lock();
        try {
            if (hasValue || exception != null) {
                throw new IllegalStateException("Already completed");
            }
            this.exception = e;
            notReady.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public static SimpleCompletableFuture2<Void> allOf(SimpleCompletableFuture2<?>... futures) {
        return supplyAsync(() -> {
            for (SimpleCompletableFuture2<?> f : futures) {
                f.get();
            }
            return null;
        });
    }

    public V get() throws InterruptedException, Exception {
        lock.lock();
        try {
            while (!hasValue && exception == null) {
                notReady.await();
            }
            if (exception != null) {
                throw exception;
            }
            return val;
        } finally {
            lock.unlock();
        }
    }
}
