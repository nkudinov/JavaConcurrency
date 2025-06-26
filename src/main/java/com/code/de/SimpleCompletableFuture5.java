package com.code.de;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SimpleCompletableFuture5<T> {

    private T value;
    private boolean isCompleted = false;

    private final Lock lock = new ReentrantLock();
    private final Condition completedCondition = lock.newCondition();

    public SimpleCompletableFuture5() {
    }

    public SimpleCompletableFuture5(T value) {
        this.value = value;
        this.isCompleted = true;
    }

    public T get() throws InterruptedException, ExecutionException {
        lock.lock();
        try {
            while (!isCompleted) {
                completedCondition.await();
            }
            return value;
        } finally {
            lock.unlock();
        }
    }

    public static <V> SimpleCompletableFuture5<V> supplyAsync(Callable<V> task) {
        SimpleCompletableFuture5<V> future = new SimpleCompletableFuture5<>();

        Thread thread = new Thread(() -> {
            try {
                V result = task.call();
                future.complete(result);
            } catch (Exception e) {
                throw new RuntimeException("Task execution failed", e);
            }
        });

        thread.start();
        return future;
    }

    private void complete(T value) {
        lock.lock();
        try {
            if (!isCompleted) {
                this.value = value;
                this.isCompleted = true;
                completedCondition.signalAll();
            }
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        SimpleCompletableFuture5<String> future = SimpleCompletableFuture5.supplyAsync(() -> "hello");
        System.out.println(future.get());
    }

}
