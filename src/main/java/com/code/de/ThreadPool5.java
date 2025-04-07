package com.code.de;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadPool5 {

    private final int capacity;
    private final AtomicBoolean isRunning;
    private final BlockingQueue<Runnable> tasks;
    List<Thread> workers;

    public ThreadPool5(int capacity) {
        this.capacity = capacity;
        this.isRunning = new AtomicBoolean(true);
        this.tasks = new LinkedBlockingQueue<>();
        this.workers = new ArrayList<>();
        for (int i = 1; i <= capacity; i++) {
            Thread worker = new Thread() {
                @Override
                public void run() {
                    while (isRunning.get()) {
                        try {
                            Runnable task = tasks.take();
                            task.run();
                        } catch (InterruptedException e) {
                            if (!isRunning.get()) {
                                throw new RuntimeException(e);
                            }
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            };
            worker.start();
            workers.add(worker);
        }
    }

    public void execute(Runnable task) {
        tasks.add(task);
    }

    public void shutdown() {
        isRunning.set(false);
        for (Thread thread : workers) {
            thread.interrupt();
        }
    }

    public static void main(String[] args) {
        ThreadPool5 threadPool5 = new ThreadPool5(10);
        for (int i = 1; i < 100; i++) {
            threadPool5.execute(() -> System.out.println(Integer.toString(11)));
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        threadPool5.shutdown();
    }
}
