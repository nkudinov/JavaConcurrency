package com.code.de;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SimpleCompletableFuture1<T> {


    private T value;
    private Throwable exception;
    private boolean isCompleted;

    private final Lock lock = new ReentrantLock();
    private final Condition completedCondition = lock.newCondition();

    private static final ExecutorService DEFAULT_EXECUTOR = Executors.newCachedThreadPool();

    public SimpleCompletableFuture1() {}

    public SimpleCompletableFuture1(Callable<T> task) {
        this(task, DEFAULT_EXECUTOR);
    }

    public SimpleCompletableFuture1(Callable<T> task, Executor executor) {
        executor.execute(() -> {
            try {
                T result = task.call();
                complete(result);
            } catch (Throwable ex) {
                completeExceptionally(ex);
            }
        });
    }

    public static <V> SimpleCompletableFuture1<V> supplyAsync(Callable<V> task) {
        return new SimpleCompletableFuture1<>(task);
    }

    public static <V> SimpleCompletableFuture1<V> supplyAsync(Callable<V> task, Executor executor) {
        return new SimpleCompletableFuture1<>(task, executor);
    }

    public void complete(T value) {
        lock.lock();
        try {
            if (isCompleted) throw new IllegalStateException("Already completed");
            this.value = value;
            this.isCompleted = true;
            completedCondition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void completeExceptionally(Throwable ex) {
        lock.lock();
        try {
            if (isCompleted) throw new IllegalStateException("Already completed");
            this.exception = ex;
            this.isCompleted = true;
            completedCondition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public T get() throws InterruptedException, ExecutionException {
        lock.lock();
        try {
            while (!isCompleted) {
                completedCondition.await();
            }
            if (exception != null) {
                throw new ExecutionException(exception);
            }
            return value;
        } finally {
            lock.unlock();
        }
    }

    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        lock.lock();
        try {
            long nanos = unit.toNanos(timeout);
            while (!isCompleted) {
                if (nanos <= 0L) {
                    throw new TimeoutException("Timeout waiting for result");
                }
                nanos = completedCondition.awaitNanos(nanos);
            }
            if (exception != null) {
                throw new ExecutionException(exception);
            }
            return value;
        } finally {
            lock.unlock();
        }
    }

    public boolean isDone() {
        lock.lock();
        try {
            return isCompleted;
        } finally {
            lock.unlock();
        }
    }

    public boolean isCompletedExceptionally() {
        lock.lock();
        try {
            return isCompleted && exception != null;
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        SimpleCompletableFuture1<String> future = SimpleCompletableFuture1.supplyAsync(() -> {
            Thread.sleep(100); // simulate some delay
            return "Hello, World!";
        });

        try {
            String result = future.get();
            System.out.println("Result: " + result);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
