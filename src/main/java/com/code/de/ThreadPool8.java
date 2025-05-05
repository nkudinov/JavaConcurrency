package com.code.de;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadPool8 {

    private static class Task<T> {
        private final Runnable task;
        private final Callable<T> callable;
        private final CompletableFuture<T> future;

        public Task(Callable<T> callable) {
            this.callable = callable;
            this.task = null;
            this.future = new CompletableFuture<>();
        }

        public Task(Runnable task) {
            this.task = task;
            this.callable = null;
            this.future = new CompletableFuture<>();
        }

        public CompletableFuture<T> getFuture() {
            return future;
        }
    }

    private final Thread[] workers;
    private final BlockingQueue<Task<?>> tasks;
    private final AtomicBoolean isRunning = new AtomicBoolean(true);

    private class Worker extends Thread {
        @Override
        public void run() {
            while (isRunning.get()) {
                try {
                    Task<?> task = tasks.take();
                    if (task.task != null) {
                        task.task.run();
                        task.future.complete(null);
                    } else if (task.callable != null) {
                        @SuppressWarnings("unchecked")
                        Task<Object> t = (Task<Object>) task;
                        t.future.complete(t.callable.call());
                    }
                } catch (Exception e) {
                    // Optionally log the error
                }
            }
        }
    }

    public ThreadPool8(int numThreads, int capacity) {
        this.tasks = new ArrayBlockingQueue<>(capacity);
        this.workers = new Thread[numThreads];
        for (int i = 0; i < numThreads; i++) {
            workers[i] = new Worker();
            workers[i].start();
        }
    }

    public void execute(Runnable task) {
        try {
            tasks.put(new Task<>(task));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore the interrupted status
        }
    }

    public <T> CompletableFuture<T> submit(Callable<T> callable) {
        Task<T> task = new Task<>(callable);
        try {
            tasks.put(task);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            task.getFuture().completeExceptionally(e);
        }
        return task.getFuture();
    }

    public void shutdown() {
        isRunning.set(false);
        for (Thread worker : workers) {
            worker.interrupt();
        }
    }
}
