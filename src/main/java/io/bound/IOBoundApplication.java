package io.bound;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class IOBoundApplication {
    private static final int NUMBER_OF_TASKS = 10_000;


    public static void main(String[] args) {
//        Scanner s = new Scanner(System.in);
//        System.out.println("Press enter to start");
//        s.nextLine();

        long start = System.currentTimeMillis();
        performTask();
        long end = System.currentTimeMillis();
        System.out.printf("Tasks took %dms to complete\n", end - start);
    }

    private static void performTask() {
        try (ExecutorService executorService = Executors.newFixedThreadPool(1000)) {
            for (int i = 0; i < NUMBER_OF_TASKS; i++) {
                executorService.submit(IOBoundApplication::blockingIoOperation);
            }
        }
    }

    // Simalates a long blocking IO
    private static void blockingIoOperation() {
        System.out.println("Executing a blocking task from thread: " + Thread.currentThread());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
