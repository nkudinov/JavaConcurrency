package com.code.de;

import java.util.LinkedList;
import java.util.Queue;

public class ConcurrentQueue<T> {
    private final Queue<T> queue = new LinkedList<>();

    public void add(T item) {
        synchronized (queue) {
            queue.add(item);
        }
    }

    public T poll() {
        synchronized (queue) {
            return queue.poll(); // Вернёт null, если пусто
        }
    }

    public T peek() {
        synchronized (queue) {
            return queue.peek();
        }
    }

    public boolean isEmpty() {
        synchronized (queue) {
            return queue.isEmpty();
        }
    }

    public int size() {
        synchronized (queue) {
            return queue.size();
        }
    }
}
