package object.as.condition.variable.countdownlatch;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SimpleCountDownLatchLockCondition implements Latch {
    private volatile int count;
    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    public SimpleCountDownLatchLockCondition(int count) {
        this.count = count;
        if (count < 0) {
            throw new IllegalArgumentException("count cannot be negative");
        }
    }

    @Override
    public void await() throws InterruptedException {
        if (getCount() == 0) return;
        while (getCount() > 0) {
            lock.lock();
            try {
                condition.await();
            } finally {
                lock.unlock();
            }
        }
    }

    @Override
    public void countDown() {
        if (getCount() > 1) {
            lock.lock();
            try {
                count--;
            } finally {
                lock.unlock();
            }
        } else if (getCount() == 1) {
            lock.lock();
            try {
                count--;
                condition.signalAll();
            } finally {
                lock.unlock();
            }
        }
    }

    @Override
    public synchronized int getCount() {
        return count;
    }
}
