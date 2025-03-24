package com.code.de;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadPool3 {

    private final int capacity;
    private final BlockingQueue<FutureTask<?>> tasks;
    private final List<Worker> workers;
    private final AtomicBoolean isRunning;

    public ThreadPool3(int capacity) {
        this.capacity = capacity;
        this.tasks = new LinkedBlockingQueue<>();
        this.workers = new ArrayList<>();
        this.isRunning = new AtomicBoolean(true);

        for (int i = 0; i < capacity; i++) {
            Worker worker = new Worker(tasks, isRunning);
            workers.add(worker);
            worker.start();
        }
    }

    public <T> Future<T> submit(Callable<T> task) {
        if (!isRunning.get()) {
            throw new IllegalStateException("ThreadPool is shutting down, no new tasks allowed.");
        }
        FutureTask<T> futureTask = new FutureTask<>(task);
        try {
            tasks.put(futureTask);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return futureTask;
    }

    public Future<?> submit(Runnable task) {
        return submit(Executors.callable(task, null));
    }

    public void shutdown() throws InterruptedException {
        isRunning.set(false);
        for (Worker worker : workers) {
            worker.interrupt();
            worker.join();
        }
    }

    static class Worker extends Thread {
        private final BlockingQueue<FutureTask<?>> queue;
        private final AtomicBoolean isRunning;

        public Worker(BlockingQueue<FutureTask<?>> tasks, AtomicBoolean isRunning) {
            this.queue = tasks;
            this.isRunning = isRunning;
        }

        @Override
        public void run() {
            while (isRunning.get() || !queue.isEmpty()) {
                try {
                    FutureTask<?> task = queue.poll();
                    if (task != null) {
                        task.run();
                    }
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ThreadPool3 pool = new ThreadPool3(3);
        Future<Integer> future = pool.submit(() -> 42);
        try {
            System.out.println(future.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        pool.shutdown();
    }
}
