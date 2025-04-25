package locks.reenterantlock.my;

import java.util.concurrent.locks.ReentrantLock;

public class CheckBehavior {
    private static int count = 0;
    private static final ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) {
        Thread incrementThread = new Thread(() -> {
            for (int i = 0; i < 1000000; i++) {
                increment();
            }
        });
        Thread decrementThread = new Thread(() -> {
            for (int i = 0; i < 1000000; i++) {
                derement();
            }
        });

        incrementThread.start();
        decrementThread.start();
        try {
            incrementThread.join();
            decrementThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println(count);
    }

    public static void increment() {
        if (lock.isLocked()) {
            System.out.println("Thread is trying to increment but is blocked");
        }
        lock.lock();
        try {
            count++;
        } finally {
            lock.unlock();
        }
    }

    public static void derement() {
        if (lock.isLocked()) {
            System.out.println("Thread is trying to decrement but is blocked");
        }
        lock.lock();
        try {
            count--;
        } finally {
            lock.unlock();
        }
    }
}
