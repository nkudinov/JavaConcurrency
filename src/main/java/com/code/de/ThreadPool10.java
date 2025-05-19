package com.code.de;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadPool10 {

    private final int capacity;
    private final BlockingQueue<Entry<?>> queue;
    private final AtomicBoolean isRunning;
    private final Thread[] workers;

    // Entry class for tasks
    private static class Entry<T> {
        private final Runnable runnable;
        private final Callable<T> callable;
        private final CompletableFuture<T> future;

        public Entry(Runnable runnable) {
            this.runnable = runnable;
            this.callable = null;
            this.future = new CompletableFuture<>();
        }

        public Entry(Callable<T> callable) {
            this.runnable = null;
            this.callable = callable;
            this.future = new CompletableFuture<>();
        }
    }

    // Worker thread class
    private class Worker extends Thread {
        @Override
        public void run() {
            while (isRunning.get() || !queue.isEmpty()) {
                try {
                    Entry<?> entry = queue.poll(500, TimeUnit.MILLISECONDS);
                    if (entry != null) {
                        try {
                            if (entry.runnable != null) {
                                entry.runnable.run();
                                entry.future.complete(null);
                            } else if (entry.callable != null) {
                                Object result = entry.callable.call();
                                ((CompletableFuture<Object>) entry.future).complete(result);
                            }
                        } catch (Exception e) {
                            entry.future.completeExceptionally(e);
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    e.printStackTrace(); // Log unexpected errors
                }
            }
        }
    }

    // Constructor
    public ThreadPool10(int capacity) {
        this.capacity = capacity;
        this.queue = new LinkedBlockingQueue<>(capacity);
        this.isRunning = new AtomicBoolean(true);
        this.workers = new Thread[capacity];
        for (int i = 0; i < capacity; i++) {
            workers[i] = new Worker();
            workers[i].start();
        }
    }

    // Submit a callable task
    public <V> CompletableFuture<V> submit(Callable<V> callable) {
        if (callable == null) throw new IllegalArgumentException("Callable must not be null");
        Entry<V> entry = new Entry<>(callable);
        try {
            queue.put(entry); // blocks if full
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalArgumentException("Thread interrupted while submitting task", e);
        }
        return entry.future;
    }

    // Submit a runnable task
    public CompletableFuture<Void> execute(Runnable runnable) {
        if (runnable == null) throw new IllegalArgumentException("Runnable must not be null");
        Entry<Void> entry = new Entry<>(runnable);
        try {
            queue.put(entry);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RejectedExecutionException("Thread interrupted while submitting task", e);
        }
        return entry.future;
    }

    // Shutdown the pool
    public void shutdown() throws InterruptedException {
        isRunning.set(false);
        for (Thread t : workers) {
            t.join();
        }
    }
}
