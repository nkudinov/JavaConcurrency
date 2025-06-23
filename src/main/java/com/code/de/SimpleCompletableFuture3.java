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
    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private Runnable completionCallback;

    public SimpleCompletableFuture3() {
    }

    public SimpleCompletableFuture3(T value) {
        this.value = value;
        this.hasCompleted = true;
    }

    public static <V> SimpleCompletableFuture3<V> supplyAsync(Callable<V> callable) {
        SimpleCompletableFuture3<V> res = new SimpleCompletableFuture3<>();
        Thread t = new Thread(() -> {
            V result;
            try {
                result = callable.call(); // Run outside lock
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            res.lock.lock();
            try {
                res.value = result;
                res.hasCompleted = true;
                res.condition.signalAll();
                if (res.completionCallback != null) {
                    res.completionCallback.run();
                }
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
        lock.lock();
        try {
            get();
            if (hasCompleted) {
                runnable.run();
            } else {
                completionCallback = runnable;
            }
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        var res = SimpleCompletableFuture3.supplyAsync(() -> {
            Thread.sleep(100); // simulate some delay
            return "hello";
        });

        res.whenComplete(() -> System.out.println("Completed!"));

        System.out.println(res.get());
    }
}
