package com.code.de;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadPool {

    static class Worker extends Thread {

        private final BlockingQueue<Runnable> queue;

        Worker(BlockingQueue<Runnable> q) {
            this.queue = q;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    Runnable task = queue.take();  // Blocks until a task is available
                    task.run();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore interrupted status
            }
        }
    }

    private final int capacity;
    private final BlockingQueue<Runnable> tasks;

    public ThreadPool(int capacity) {
        this.capacity = capacity;
        tasks = new LinkedBlockingQueue<>(capacity);
        for (int i = 0; i < capacity; i++) {
            Worker worker = new Worker(tasks);
            worker.start();
        }
    }

    public void submit(Runnable task) throws InterruptedException {
        tasks.put(task);
    }
}
