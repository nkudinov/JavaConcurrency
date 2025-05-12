package com.code.de;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadPool9 {

    private final int capacity;

    private class Entry<T> {

        private final Runnable runnable;
        private final Callable<T> callable;
        private final CompletableFuture<T> future;

        private Entry(Runnable runnable, Callable<T> callable) {
            this.runnable = runnable;
            this.callable = callable;
            this.future = new CompletableFuture<>();
        }

        public Entry(Callable<T> callable) {
            this(null, callable);
        }

        public Entry(Runnable runnable) {
            this(runnable, null);
        }

    }

    private final BlockingQueue<Entry<?>> queue;

    private final AtomicBoolean isRunning;

    private class Worker extends Thread {

        private final BlockingQueue<Entry<?>> queue;
        private final AtomicBoolean isRunning;

        private Worker(BlockingQueue<Entry<?>> queue, AtomicBoolean isRunning) {
            super("worker");
            this.queue = queue;
            this.isRunning = isRunning;
        }

        @Override
        public void run() {
            while (isRunning.get()) {
                try {
                    Entry<?> entry = queue.poll(100, TimeUnit.MICROSECONDS);
                    if (entry != null) {
                        if (entry.runnable != null) {
                            entry.runnable.run();
                            entry.future.complete(null);
                        } else if (entry.callable != null) {
                            try {
                                Object result = entry.callable.call();
                                ((CompletableFuture<Object>) entry.future).complete(result);
                            } catch (Exception e) {
                                entry.future.completeExceptionally(e);
                            }
                        }
                    }
                } catch (Exception e) {

                }
            }
        }
    }

    private final Thread[] workers;

    public ThreadPool9(int capacity) {
        this.capacity = capacity;
        this.isRunning = new AtomicBoolean(true);
        this.queue = new LinkedBlockingQueue<>(capacity);
        this.workers = new Thread[capacity];
        for (int i = 0; i < capacity; i++) {
            workers[i] = new Worker(queue, isRunning);
            workers[i].start();
        }
    }

    public void execute(Runnable task) {
        Entry entry = new Entry(task);
        queue.add(entry);
    }

    public <T> Future<T> submit(Callable<T> callable) {
        Entry entry = new Entry(callable);
        queue.add(entry);
        return entry.future;
    }

    public void shutdown() {
        isRunning.set(false);
        for (int i = 0; i < capacity; i++) {
            workers[i].interrupt();
            try {
                workers[i].join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ThreadPool9 threadPool9 = new ThreadPool9(10);
        System.out.println(threadPool9.submit(() -> {return  "hello";}).get());
        threadPool9.shutdown();
    }
}
