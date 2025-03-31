package com.code.de;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadPool4 {

    private BlockingQueue<Runnable> tasks;
    private AtomicBoolean isRunning;

    WorkerThead[] workers;

    public ThreadPool4(int capacity) {
        this.capacity = capacity;
        this.tasks = new LinkedBlockingQueue<>();
        this.isRunning = new AtomicBoolean(true);
        this.workers = new WorkerThead[capacity];
        for (int i = 0; i < workers.length; i++) {
            workers[i] = new WorkerThead(tasks, isRunning);
            workers[i].start();
        }
    }

    private int capacity;

    public void execute(Runnable task) {
        tasks.add(task);
    }

    public void shutdown() throws InterruptedException {
        isRunning.set(false);
        for (WorkerThead t : this.workers) {
            t.interrupt();
            t.join();
        }
    }

    static class WorkerThead extends Thread {

        BlockingQueue<Runnable> queue;
        AtomicBoolean isRunning;

        public WorkerThead(BlockingQueue<Runnable> queue, AtomicBoolean isRunning) {
            this.queue = queue;
            this.isRunning = isRunning;
        }

        @Override
        public void run() {
            while (isRunning.get()) {
                Runnable task = null;
                try {
                    task = queue.take();
                    task.run();
                } catch (InterruptedException ignored) {

                }

            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ThreadPool4 threadPool4 = new ThreadPool4(10);
        threadPool4.execute(() -> System.out.println("hello"));
        Thread.sleep(1000);
        threadPool4.shutdown();
    }

}
