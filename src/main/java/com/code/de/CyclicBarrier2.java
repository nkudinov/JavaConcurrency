package com.code.de;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class CyclicBarrier2 {

    private final int original;
    private int count;
    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();

    public CyclicBarrier2(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("Barrier count must be greater than zero");
        }
        this.original = count;
        this.count = count;
    }

    public void await() {
        lock.lock();
        try {
            count--;
            if (count == 0) {
                count = original;
                condition.signalAll();
            } else {
                while (count != original) {
                    condition.await();
                }
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        CyclicBarrier2 cyclicBarrier2 = new CyclicBarrier2(3);
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        executorService.execute(cyclicBarrier2::await);
        executorService.execute(cyclicBarrier2::await);
        executorService.execute(cyclicBarrier2::await);
        executorService.shutdown();
    }
}
