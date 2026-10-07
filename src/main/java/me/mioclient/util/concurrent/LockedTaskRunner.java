package me.mioclient.util.concurrent;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockedTaskRunner {
    public static final Lock lock = new ReentrantLock();

    public static void executeLocked(java.lang.Runnable runnable) {
        runWithLock(lock, runnable);
    }

    public static java.lang.Runnable wrapWithDefaultLock(java.lang.Runnable runnable) {
        return () -> {
            executeLocked(runnable);
        };
    }

    public static void runWithLock(Lock lock2, java.lang.Runnable runnable) {
        try {
            lock2.lock();
            runnable.run();
        } finally {
            lock2.unlock();
        }
    }

    public static java.lang.Runnable wrapWithLock(Lock lock2, java.lang.Runnable runnable) {
        return () -> {
            runWithLock(lock2, runnable);
        };
    }
}
