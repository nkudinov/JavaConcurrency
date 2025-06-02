package com.code.de;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadPool11 {

    private class Entry<V> {
        private final Runnable runnable;
        private final Callable<V> callable;
        private final CompletableFuture<V> future;

        public Entry(Runnable runnable, Callable<V> callable) {
            this.runnable = runnable;
            this.callable = callable;
            this.future = new CompletableFuture<>();
        }

        public Entry(Callable<V> callable) {
            this(null, callable);
        }

        public Entry(Runnable runnable) {
            this(runnable, null);
        }
    }

    private final int capacity;
    private final BlockingQueue<Entry<?>> queue;
    private final AtomicBoolean isRunning;
    private final Thread[] workers;

    private class Worker extends Thread {
        @Override
        public void run() {
            while (isRunning.get()) {
                try {
                    Entry<?> entry = queue.poll(100, TimeUnit.MILLISECONDS);
                    if (entry != null) {
                        if (entry.runnable != null) {
                            try {
                                entry.runnable.run();
                                 entry.future.complete(null);
                            } catch (Exception e) {
                                entry.future.completeExceptionally(e);
                            }
                        } else if (entry.callable != null) {
                            try {
                                Object result = entry.callable.call();
                                completeFuture(entry, result);
                            } catch (Exception e) {
                                entry.future.completeExceptionally(e);
                            }
                        }
                    }
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        private <T> void completeFuture(Entry<?> entry, Object result) {
            ((CompletableFuture<T>) entry.future).complete((T) result);
        }
    }

    public ThreadPool11(int capacity, int numThreads) {
        this.capacity = capacity;
        this.queue = new LinkedBlockingQueue<>(capacity);
        this.isRunning = new AtomicBoolean(true);
        this.workers = new Thread[numThreads];
        for (int i = 0; i < numThreads; i++) {
            workers[i] = new Worker();
            workers[i].start();
        }
    }

    public <V> Future<V> submit(Callable<V> callable) throws InterruptedException {
        Entry<V> entry = new Entry<>(callable);
        queue.put(entry);
        return entry.future;
    }

    public Future<Void> execute(Runnable runnable) throws InterruptedException {
        Entry<Void> entry = new Entry<>(runnable);
        queue.put(entry);
        return entry.future;
    }

    public void shutdown() {
        isRunning.set(false);
        for (Thread worker : workers) {
            worker.interrupt();
        }
    }
}
