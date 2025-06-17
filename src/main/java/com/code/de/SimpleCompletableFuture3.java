package com.code.de;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

public class SimpleCompletableFuture3<T> {

    private boolean hasCompleted = false;
    private T value;
    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    public SimpleCompletableFuture3() {
    }

    public SimpleCompletableFuture3(T value) {
        this.value = value;
        this.hasCompleted = true;
    }

    public static <V> SimpleCompletableFuture3<V> supplyAsync(Callable<V> callable) {
        SimpleCompletableFuture3<V> res = new SimpleCompletableFuture3<>();
        Thread t = new Thread(() -> {
            try {
                res.lock.lock();
                res.hasCompleted = true;
                res.value = callable.call();
                res.condition.signalAll();
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                res.lock.unlock();
            }
        });
        t.start();
        return res;

    }

    public T get() {
        lock.lock();
        try {
            while (!hasCompleted) {
                condition.awaitUninterruptibly();
            }
            return value;
        } finally {
            lock.unlock();
        }
    }

    public void whenComplete(Runnable runnable) {

    }

    ;

    public static void main(String[] args) {
        var res = SimpleCompletableFuture3.supplyAsync(() -> {
            return "hello";
        });
        System.out.println(res.get());
    }
}
