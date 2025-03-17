package com.code.de;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadPool {

    private int capacity;
    BlockingQueue<Runnable> tasks;


    public ThreadPool(int capacity) {
        this.capacity = capacity;
        tasks = new LinkedBlockingQueue<>(capacity);
        for (int i = 1; i <= capacity; i++) {
            var worker = new Worker(tasks);
            worker.start();
        }
    }

    public void submit(Runnable task) {
        tasks.add(task);
    }

    static class Worker extends Thread {

        BlockingQueue<Runnable> q;

        Worker(BlockingQueue<Runnable> q) {
            this.q = q;
        }

        @Override
        public void run() {
            while (true) {
                Runnable task = q.poll();
                try {
                    Objects.requireNonNull(task).run();
                } catch (Exception e) {

                }
            }
        }
    }
}
