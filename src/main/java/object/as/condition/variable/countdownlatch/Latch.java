package object.as.condition.variable.countdownlatch;

public interface Latch {
    /**
     * Causes the current thread to wait until the latch has counted down to zero.
     * If the current count is already zero then this method returns immediately.
     */
    void await() throws InterruptedException;

    /**
     * Decrements the count of the latch, releasing all waiting threads when the count reaches zero.
     * If the current count already equals zero then nothing happens.
     */
    void countDown();

    /**
     * Returns the current count.
     */
    int getCount();

}
