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

        BlockingQueue<Runnable> queue;
        Lock lock;

        Worker(BlockingQueue<Runnable> q ) {
            this.queue = q;
        }

        @Override
        public void run() {
            while(true) {
                Runnable task = queue.poll();
                task.run();
            }
        }
    }

    private int capcity;

    BlockingQueue<Runnable> tasks;

    public ThreadPool(int capcity) {
        this.capcity = capcity;
        tasks = new LinkedBlockingQueue<>(capcity);
        for(int i = 1; i<=capcity;i++) {
            new Worker(tasks);
        }
    }

    public void submit(Runnable task) {
            tasks.add(task);
    }
}
