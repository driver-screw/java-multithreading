package thread.coordination.join;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        List<Long> inputNumbers = Arrays.asList(1000000L, 3435L, 34343L, 2324L, 4546L, 23L, 5556L);
        List<FactorialThread> threads = new ArrayList<>();

        for (Long inputNumber : inputNumbers) {
            threads.add(new FactorialThread(inputNumber));
        }

        for (FactorialThread thread : threads) {
            thread.start();
        }

        for (FactorialThread thread : threads) {
            thread.join(2000);
        }


        for (int i = 0; i < inputNumbers.size(); i++) {
            FactorialThread factorialThread = threads.get(i);
            if (factorialThread.isFinished()) {
                System.out.println("Factorial of " + inputNumbers.get(i) + " is " + factorialThread.getResult());
            } else {
                System.out.println("Factorial of " + inputNumbers.get(i) + " is being calculating");

            }
        }

        for (FactorialThread thread : threads) {
            thread.interrupt();
        }


    }
}
