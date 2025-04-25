package locks.reentrantreadwritelock;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    public static final int HIGHEST_PRICE = 1000;

    public static void main(String[] args) {
        InventoryDatabase inventoryDatabase = new InventoryDatabase();
        Random random = new Random();
        for (int i = 0; i < 10000; i++) {
            inventoryDatabase.addItem(random.nextInt(HIGHEST_PRICE));
        }

        Thread writerThread = new Thread(() -> {
            while (true) {
                inventoryDatabase.addItem(random.nextInt(HIGHEST_PRICE));
                inventoryDatabase.removeItem(random.nextInt(HIGHEST_PRICE));
                try {
                    Thread.sleep(10);
                } catch (InterruptedException _) {

                }
            }
        }
        );
        writerThread.setDaemon(true);
        writerThread.start();

        int numberOfReaderThreads = 7;
        List<Thread> readerThreads = new ArrayList<>();
        for (int i = 0; i < numberOfReaderThreads; i++) {
            Thread readerThread = new Thread(() -> {
                for (int j = 0; j < 100000; j++) {
                    int upperBound = random.nextInt(HIGHEST_PRICE);
                    int lowerBound = upperBound > 0 ? random.nextInt(upperBound) : 0;

                    int numberOfItemsInPriceRange = inventoryDatabase.getNumberOfItemsInPriceRange(lowerBound, upperBound);

                }
            });
            readerThread.setDaemon(true);
            readerThreads.add(readerThread);
        }

        long startTime = System.currentTimeMillis();
        for (Thread readerThread : readerThreads) {
            readerThread.start();
        }
        for (Thread readerThread : readerThreads) {
            try {
                readerThread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        System.out.println(String.format("Duration: %d ms", duration));
    }

    public static class InventoryDatabase {
        private TreeMap<Integer, Integer> priceToCountMap = new TreeMap<>();
        private Lock lock = new ReentrantLock();

        public int getNumberOfItemsInPriceRange(int lowerBound, int upperBound) {
            lock.lock();
            try {
                Integer fromKey = priceToCountMap.ceilingKey(lowerBound);
                Integer toKey = priceToCountMap.floorKey(upperBound);
                if (fromKey == null || toKey == null) {
                    return 0;
                }
                NavigableMap<Integer, Integer> rangeOfPrices = priceToCountMap.subMap(fromKey, true, toKey, true);
                int sum = 0;
                for (int numberOfItemsForPrice : rangeOfPrices.values()) {
                    sum += numberOfItemsForPrice;
                }
                return sum;
            } finally {
                lock.unlock();
            }

        }

        public void addItem(int price) {
            lock.lock();
            try {
                priceToCountMap.merge(price, 1, Integer::sum);
            } finally {
                lock.unlock();
            }
        }

        public void removeItem(int price) {
            lock.lock();
            try {
                Integer numberOfItemsPerPrice = priceToCountMap.get(price);
                if (numberOfItemsPerPrice == null || numberOfItemsPerPrice == 1) {
                    priceToCountMap.remove(price);
                } else {
                    priceToCountMap.put(price, numberOfItemsPerPrice - 1);
                }
            } finally {
                lock.unlock();
            }
        }
    }
}
