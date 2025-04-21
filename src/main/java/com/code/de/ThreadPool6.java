package com.code.de;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadPool6 {

    private int capacity;
    Thread[] workers;
    private final AtomicBoolean isRunning;
    private final BlockingQueue<Runnable> tasks;

    public ThreadPool6(int capacity) {
        this.capacity = capacity;
        this.tasks = new LinkedBlockingQueue<>(capacity);
        this.isRunning = new AtomicBoolean(true);
        this.workers = new Thread[capacity];
        for (int i = 0; i < capacity; i++) {
            workers[i] = new Thread() {
                @Override
                public void run() {
                    while (isRunning.get()) {
                        Runnable task = null;
                        try {
                            task = tasks.poll(100, TimeUnit.MICROSECONDS);
                        } catch (InterruptedException e) {

                        }
                        if (task != null) {
                            try {
                                task.run();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            };
            workers[i].start();
        }
    }

    public void execute(Runnable task) throws InterruptedException {
        if (task == null) {
            throw new IllegalArgumentException("Task could not be null");
        }
        tasks.put(task);
    }

    public void shutdown() throws InterruptedException {
        isRunning.set(false);
        for (int i = 0; i < capacity; i++) {
            workers[i].interrupt();
            workers[i].join();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ThreadPool6 threadPool6 = new ThreadPool6(3);
        threadPool6.execute( ()-> System.out.println("hello"));
        Thread.sleep(1000);
        threadPool6.shutdown();
    }
}
