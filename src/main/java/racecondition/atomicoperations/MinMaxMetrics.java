package racecondition.atomicoperations;

public class MinMaxMetrics {
    private volatile long min = -1;
    private volatile long max = -1;
    private final Object lockMin = new Object();
    private final Object lockMax = new Object();


    public void addSample(long newSample) {
        if (newSample < min) {
            synchronized (lockMin) {
                min = newSample;
            }
        }
        if (newSample > max) {
            synchronized (lockMax) {
                max = newSample;
            }
        }
    }


    public long getMin() {
        return min;
    }


    public long getMax() {
        return max;
    }
}
