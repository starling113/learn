package org.lingg.learn.concurrent;

import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockTest {
    static ReentrantLock lock = new ReentrantLock();
    public static void main(String[] args) {
        lock.lock();

        try {

        }finally {
            lock.unlock();
        }
    }
}
