package object.as.condition.variable.countdownlatch;

public class LatchCheck {

    public static void main(String[] args) throws InterruptedException {
        Latch latch = new SimpleCountDownLatchObjectCondition(2);
        CheckThread t1 = new CheckThread(latch);
        CheckThread t2 = new CheckThread(latch);

        t1.start();
        Thread.sleep(1000);
        t2.start();
        Thread.sleep(1000);

        latch.countDown();
        System.out.println("Latch count is " + latch.getCount());
        Thread.sleep(1000);
        latch.countDown();
        System.out.println("Latch count is " + latch.getCount());

    }

    private static class CheckThread extends Thread {
        private Latch latch;

        public CheckThread(Latch latch) {
            this.latch = latch;
        }

        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName() + " thread started");
            try {
                latch.await();
            } catch (InterruptedException _) {
            }
            System.out.println(Thread.currentThread().getName() + " thread continued");
        }
    }
}
