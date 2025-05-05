package object.as.condition.variable;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.StringJoiner;

public class MatricesMultiplierApp {
    private static final int N = 10;
    private static final String INPUT_FILE = "./target/matrices";
    private static final String OUTPUT_FILE = "./target/matrices_results.txt";
    private static final int MAX_QUEUE_CAPACITY = 5;

    public static void main(String[] args) throws IOException {
        ThreadSafeQueue threadSafeQueue = new ThreadSafeQueue();
        File inputFile = new File(INPUT_FILE);
        File outputFile = new File(OUTPUT_FILE);

        MatricesReaderProducer matricesReaderProducer =
                new MatricesReaderProducer(new FileReader(inputFile), threadSafeQueue);
        MatricesMultiplierConsumer matricesMultiplierConsumer =
                new MatricesMultiplierConsumer(threadSafeQueue, new FileWriter(outputFile));

        matricesReaderProducer.start();
        matricesMultiplierConsumer.start();
    }

    private static class MatricesMultiplierConsumer extends Thread {
        private ThreadSafeQueue queue;
        private FileWriter writer;

        public MatricesMultiplierConsumer(ThreadSafeQueue queue, FileWriter writer) {
            this.queue = queue;
            this.writer = writer;
        }

        @Override
        public void run() {
            while (true) {
                MatricesPair matricesPair = queue.remove();
                if (matricesPair == null) {
                    System.out.println("There are no matrices to read from th queue, consumer is terminating");
                    break;
                }
                float[][] multiplied = multiplyMatrices(matricesPair.matrix1, matricesPair.matrix2);
                try {
                    saveMatrixToFile(writer, multiplied);
                } catch (IOException _) {
                }
            }

            try {
                writer.flush();
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private void saveMatrixToFile(FileWriter writer, float[][] matrix) throws IOException {
            for (int r = 0; r < N; r++) {
                StringJoiner stringJoiner = new StringJoiner(", ");
                for (int c = 0; c < N; c++) {
                    stringJoiner.add(String.format("%.2f", matrix[r][c]));
                }
                writer.write(stringJoiner.toString());
                writer.write(System.lineSeparator());
            }
            writer.write(System.lineSeparator());
        }

        private float[][] multiplyMatrices(float[][] m1, float[][] m2) {
            float[][] result = new float[N][N];
            for (int r = 0; r < N; r++) {
                for (int c = 0; c < N; c++) {
                    for (int i = 0; i < N; i++) {
                        result[r][c] += m1[r][i] * m2[i][c];
                    }
                }
            }
            return result;
        }
    }

    private static class MatricesReaderProducer extends Thread {
        private Scanner scanner;
        private ThreadSafeQueue queue;

        public MatricesReaderProducer(FileReader reader, ThreadSafeQueue queue) {
            this.queue = queue;
            this.scanner = new Scanner(reader);
        }

        @Override
        public void run() {
            while (true) {
                float[][] matrix1 = readMatrix();
                float[][] matrix2 = readMatrix();

                if (matrix1 == null || matrix2 == null) {
                    queue.terminate();
                    System.out.println("No more matrices to read. Producer thread is terminating");
                    return;
                }
                MatricesPair matricesPair = new MatricesPair();
                matricesPair.matrix1 = matrix1;
                matricesPair.matrix2 = matrix2;
                queue.add(matricesPair);

            }
        }

        private float[][] readMatrix() {
            float[][] matrix = new float[N][N];
            for (int r = 0; r < N; r++) {
                if (!scanner.hasNext()) {
                    return null;
                }
                String[] line = scanner.nextLine().split(",");
                for (int c = 0; c < N; c++) {
                    matrix[r][c] = Float.parseFloat(line[c]);
                }
            }
            scanner.nextLine();
            return matrix;
        }
    }

    private static class ThreadSafeQueue {
        private Queue<MatricesPair> queue = new LinkedList<>();
        private boolean isEmpty = true;
        private boolean isTerminated = false;

        public synchronized void add(MatricesPair matricesPair) {
            while (queue.size() == MAX_QUEUE_CAPACITY) {
                try {
                    wait();
                } catch (InterruptedException _) {

                }
            }
            queue.add(matricesPair);
            isEmpty = false;
            notify();
        }

        public synchronized MatricesPair remove() {

            while (isEmpty && !isTerminated) {
                try {
                    wait();
                } catch (InterruptedException _) {
                }
            }
            if (queue.size() == 1) {
                isEmpty = true;
            }
            if (queue.isEmpty() && isTerminated) {
                return null;
            }

            System.out.println("Queue size is " + queue.size());
            MatricesPair matricesPair = null;
            matricesPair = queue.remove();

            if (queue.size() == MAX_QUEUE_CAPACITY - 1) {
                notifyAll();
            }
            return matricesPair;
        }

        public synchronized void terminate() {
            isTerminated = true;
            notifyAll();
        }
    }

    private static class MatricesPair {
        private float[][] matrix1;
        private float[][] matrix2;
    }
}
