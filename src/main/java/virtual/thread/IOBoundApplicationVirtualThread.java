package virtual.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class IOBoundApplicationVirtualThread {
    private static final int NUMBER_OF_TASKS = 10_000;


    public static void main(String[] args) {

        long start = System.currentTimeMillis();
        performTask();
        long end = System.currentTimeMillis();
        System.out.printf("Tasks took %dms to complete\n", end - start);
    }

    private static void performTask() {
        try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < NUMBER_OF_TASKS; i++) {
                executorService.submit(() -> {
                    for (int j = 0; j < 100; j++) {
                        blockingIoOperation();
                    }
                });
            }
        }
    }

    // Simalates a long blocking IO
    private static void blockingIoOperation() {
        System.out.println("Executing a blocking task from thread: " + Thread.currentThread());
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
