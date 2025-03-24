package com.code.de;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadPool3 {

    private final int capacity;
    private final BlockingQueue<Runnable> tasks;
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

    public void submit(Runnable task) {
        if (isRunning.get()) {
            try {
                tasks.put(task);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        } else {
            throw new IllegalStateException("ThreadPool is shutting down, no new tasks allowed.");
        }
    }

    public void shutdown() throws InterruptedException {
        isRunning.set(false);
        for (Worker worker : workers) {
            worker.interrupt();
            worker.join();
        }
    }

    static class Worker extends Thread {
        private final BlockingQueue<Runnable> queue;
        private final AtomicBoolean isRunning;

        public Worker(BlockingQueue<Runnable> tasks, AtomicBoolean isRunning) {
            this.queue = tasks;
            this.isRunning = isRunning;
        }

        @Override
        public void run() {
            while (isRunning.get() || !queue.isEmpty()) {
                try {
                    Runnable task = queue.poll();
                    if (task != null) {
                        task.run();
                    }
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}

