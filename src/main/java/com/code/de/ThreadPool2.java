package com.code.de;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadPool2 {

    private final int capacity;
    private final BlockingQueue<Runnable> tasks;
    private final List<Thread> workers;
    private AtomicBoolean isRunning;

    public ThreadPool2(int capacity) {
        this.capacity = capacity;
        this.tasks = new LinkedBlockingQueue<>(capacity);
        this.workers = new ArrayList<>();
        this.isRunning.set(true);

        for (int i = 0; i < this.capacity; i++) {
            Thread thread = new Worker();
            thread.start();
            workers.add(thread);
        }
    }

    public void submit(Runnable task) {
        if (isRunning.get()) {
            tasks.offer(task);
        } else {
            throw new IllegalStateException("ThreadPool is shutting down and cannot accept new tasks.");
        }
    }

    public void shutdown() {
        isRunning.set(false);
        for (Thread worker : workers) {
            tasks.add(new PoisonMessage());
        }
    }

    private class Worker extends Thread {
        @Override
        public void run() {
            while (isRunning.get()) {
                try {
                    Runnable task = tasks.take(); // Blocking call
                    if (task instanceof PoisonMessage) {
                      break;
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Preserve interrupt status
                }
            }
        }
    }
    static class PoisonMessage implements Runnable {
        @Override
        public void run() {

        }
    }
}
