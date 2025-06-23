package com.code.de;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadPool12 {

    private class Entry<T> {

        private final Callable<T> callable;
        private final Runnable runnable;
        private CompletableFuture<T> completableFuture;

        public Entry(Callable<T> callable, Runnable runnable) {
            this.callable = callable;
            this.runnable = runnable;
            this.completableFuture = new CompletableFuture<>();
        }

        public Entry(Callable<T> callable) {
            this(callable, null);
        }

        public Entry(Runnable runnable) {
            this(null, runnable);
        }
    }

    private class Worker extends Thread {

        private AtomicBoolean isRunning;

        @Override
        public void run() {
            Entry entry = null;
            while (!isInterrupted() && isRunning.get()) {
                try {
                    entry = queue.poll(100, TimeUnit.MICROSECONDS);
                    if (entry != null) {
                        if (!entry.completableFuture.isDone()) {
                            process(entry);
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private <T> void process(Entry<T> entry) throws Exception {
        if (entry.runnable != null) {
            entry.runnable.run();
            entry.completableFuture.complete(null);
        } else if (entry.callable != null) {
            T obj = entry.callable.call();
            entry.completableFuture.complete(obj);
        }
    }


    public ThreadPool12() {
        queue = new LinkedBlockingQueue<>();
    }

    private BlockingQueue<Entry<?>> queue;

    public <V> Future<V> submit(Callable<V> callable) throws InterruptedException {
        Entry entry = new Entry(callable);
        queue.put(entry);
        return (Future<V>) entry.completableFuture;
    }

    public void submit(Runnable runnable) throws InterruptedException {
        Entry entry = new Entry(runnable);
        queue.put(entry);
    }
}
