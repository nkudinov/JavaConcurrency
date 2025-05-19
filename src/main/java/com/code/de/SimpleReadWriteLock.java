package com.code.de;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SimpleReadWriteLock {

    private final Lock lock;
    private final Condition canRead;
    private final Condition canWrite;

    private int numReaders;
    private boolean isWriting;

    public SimpleReadWriteLock() {
        lock = new ReentrantLock();
        canRead = lock.newCondition();
        canWrite = lock.newCondition();
        numReaders = 0;
        isWriting = false;
    }

    public void takeReadLock() {
        lock.lock();
        try {
            while (isWriting) {
                canRead.await();
            }
            numReaders++;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Proper interrupt handling
        } finally {
            lock.unlock();
        }
    }

    public void releaseReadLock() {
        lock.lock();
        try {
            numReaders--;
            if (numReaders == 0) {
                canWrite.signal();
            }
        } finally {
            lock.unlock();
        }
    }

    public void takeWriteLock() {
        lock.lock();
        try {
            while (isWriting || numReaders > 0) {
                canWrite.await();
            }
            isWriting = true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Proper interrupt handling
        } finally {
            lock.unlock();
        }
    }

    public void releaseWriteLock() {
        lock.lock();
        try {
            isWriting = false;
            canRead.signalAll(); // Let readers in
            canWrite.signal();   // Let next writer in (if any)
        } finally {
            lock.unlock();
        }
    }
}
