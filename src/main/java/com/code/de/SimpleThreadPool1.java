package com.code.de;

import com.code.de.ThreadPool3.Worker;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class SimpleThreadPool1 {

    static class Item<T> {

        protected CompletableFuture<T> future;
        private Callable<T> callable;

        public Item(Callable<T> callable) {
            this.callable = callable;
            this.future = new CompletableFuture<>();
        }
    }

    private int capacity;
    private Thread[] workers;
    private AtomicBoolean isRunning;
    private BlockingQueue<Item<?>> buffer;

    public SimpleThreadPool1(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Can not create thread pool, because capacity is <=0");
        }
        this.capacity = capacity;
        this.isRunning = new AtomicBoolean(true);
        this.workers = new Thread[capacity];
        this.buffer = new LinkedBlockingQueue<>();
        for (int i = 0; i < capacity; i++) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    while (isRunning.get()) {
                        try {
                            Item<?> cur = buffer.take();
                            runSingleElemenet(cur);
                        } catch (InterruptedException e) {
                            if (!isRunning.get()) {
                                Thread.currentThread().interrupt();
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }

                    }
                }

            };
            thread.start();
            workers[i] = thread;
        }
    }

    private <T> void runSingleElemenet(Item<T> cur) throws Exception {
        var res = cur.callable.call();
        cur.future.complete(res);
    }

    public void execute(Runnable task) {
        submit(() -> {
            task.run();
            return null;
        });
    }

    public <T> Future<T> submit(Callable<T> task) {
        Item<T> item = new Item<>(task);
        buffer.add(item);
        return item.future;
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        SimpleThreadPool1 simpleThreadPool1 = new SimpleThreadPool1(10);
        simpleThreadPool1.execute(() -> {
            System.out.println("hello");
        });
        simpleThreadPool1.submit(() -> {
                System.out.println("hello1");
                return "hello";
            }
        ).get();
        simpleThreadPool1.shutdown();
    }

    public void shutdown() throws InterruptedException {
        isRunning.set(false);
        for (Thread t : workers) {
            t.interrupt();
        }
        for (Thread t : workers) {
            t.join();
        }
    }
}
