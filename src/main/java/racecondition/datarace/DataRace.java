package racecondition.datarace;

public class DataRace {

    public static void main(String[] args) throws InterruptedException {
        SharedClass sharedClass = new SharedClass();
        int repeatNumber = Integer.MAX_VALUE;
        Thread thread1 = new Thread(() ->
        {
            for (int i = 0; i < repeatNumber; i++) {
                sharedClass.increment();
            }
        });
        Thread thread2 = new Thread(() ->
        {
            for (int i = 0; i < repeatNumber; i++) {
                sharedClass.checkForDataRace();
            }
        });
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
    }

    public static class SharedClass {
        private volatile int x = 0;
        private volatile int y = 0;

        public void increment() {
            x++;
            y++;
        }

        public void checkForDataRace() {
            if (y > x) {
                System.out.println(" y>x - Data race detected");
            }
        }
    }
}
