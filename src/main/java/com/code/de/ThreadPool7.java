package com.code.de;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadPool7 {

    private final int capacity;

    private class SimpleCompletableFuture<T> {
        private T obj;
        private boolean completed = false;
        private final Lock lock = new ReentrantLock();
        private final Condition condition = lock.newCondition();

        public T get() throws InterruptedException {
            lock.lock();
            try {
                while (!completed) {
                    condition.await();
                }
                return obj;
            } finally {
                lock.unlock();
            }
        }

        public void complete(T obj) {
            lock.lock();
            try {
                this.obj = obj;
                this.completed = true;
                condition.signalAll();
            } finally {
                lock.unlock();
            }
        }
    }

    private class Entry<T> {

        private final Callable<T> callable;
        private final SimpleCompletableFuture<T> future;

        public Entry(Callable<T> callable) {
            this.callable = callable;
            this.future = new SimpleCompletableFuture<>();
        }
    }

    BlockingQueue<Entry<?>> tasks = new LinkedBlockingQueue<>();
    private final AtomicBoolean isRunning = new AtomicBoolean(true);

    Thread[] workers;

    public ThreadPool7(int capacity) {
        this.capacity = capacity;
        this.workers = new Thread[capacity];
        this.tasks = new LinkedBlockingQueue<>(capacity);
        for (int i = 0; i < capacity; i++) {
            workers[i] = new Worker(tasks, isRunning);
            workers[i].start();
        }
    }

    public <T> SimpleCompletableFuture<T> submit(Callable<T> callable) {
        // No body required because you said "do not add more code"
        Entry<T> entry = new Entry<>(callable);
        tasks.add(entry);
        return entry.future;
    }

    private class Worker extends Thread {

        private BlockingQueue<Entry<?>> queue;
        private AtomicBoolean isRunning;

        public Worker(BlockingQueue<Entry<?>> tasks, AtomicBoolean isRunning) {
            this.queue = tasks;
            this.isRunning = isRunning;
        }

        @Override
        public void run() {
            while (isRunning.get()) {
                try {
                    Entry<Object> task = (Entry<Object>) queue.poll(100, TimeUnit.MICROSECONDS);
                    if (task != null) {
                        try {
                            task.future.complete(task.callable.call());
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
    public void shotdown() throws InterruptedException {
        isRunning.set(false);
        for(int i = 0; i < workers.length; i++) {
            workers[i].interrupt();
            workers[i].join();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ThreadPool7 threadPool7 = new ThreadPool7(10);
        System.out.println(threadPool7.submit(()-> {
            System.out.println("hello");
            return "world";
        }).get());
        threadPool7.shotdown();
    }
}
