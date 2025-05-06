package object.as.condition.variable.countdownlatch;

public class SimpleCountDownLatchObjectCondition implements Latch {

    private volatile int count;

    public SimpleCountDownLatchObjectCondition(int count) {
        this.count = count;
        if (count < 0) {
            throw new IllegalArgumentException("count cannot be negative");
        }
    }

    /**
     * Causes the current thread to wait until the latch has counted down to zero.
     * If the current count is already zero then this method returns immediately.
     */
    @Override
    public void await() throws InterruptedException {
        if (count == 0) return;
        while (count > 0) {
            synchronized (this) {
                wait();
            }
        }
    }

    /**
     * Decrements the count of the latch, releasing all waiting threads when the count reaches zero.
     * If the current count already equals zero then nothing happens.
     */
    @Override
    public void countDown() {
        if (count == 1) {
            synchronized (this) {
                count--;
                notifyAll();
            }

        } else if (count == 0) return;
        else {
            synchronized (this) {
                count--;
            }
        }
    }

    /**
     * Returns the current count.
     */
    @Override
    public synchronized int getCount() {
        return count;
    }
}
