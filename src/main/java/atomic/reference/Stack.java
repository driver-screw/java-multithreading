package atomic.reference;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

public class Stack {
    public static void main(String[] args) throws InterruptedException {
//        StandardStack<Integer> stack = new StandardStack<>();
        LockFreeStack<Integer> stack = new LockFreeStack<>();

        Random random = new Random();
        for (int i = 0; i < 100000; i++) {
            stack.push(random.nextInt());
        }

        List<Thread> threads = new ArrayList<>();

        int pushingThreads = 2;
        int poppingThreads = 2;

        for (int i = 0; i < pushingThreads; i++) {
            Thread t = new Thread(() -> {
                while (true) {
                    stack.push(random.nextInt());
                }
            });
            t.setDaemon(true);
            threads.add(t);
        }

        for (int i = 0; i < poppingThreads; i++) {
            Thread t = new Thread(() -> {
                while (true) {
                    stack.pop();
                }
            });
            t.setDaemon(true);
            threads.add(t);
        }

        for (Thread thread : threads) {
            thread.start();
        }
        Thread.sleep(10000);
        System.out.printf("%,d operations were performed", stack.getCounter());
    }


    private static class StandardStack<T> {
        private StackNode<T> head;
        private int counter = 0;

        public synchronized void push(T value) {
            StackNode<T> newHead = new StackNode<>(value);
            newHead.next = head;
            head = newHead;
            counter++;
        }

        public synchronized T pop() {
            if (head == null) {
                counter++;
                return null;
            }

            T value = head.value;
            head = head.next;
            counter++;
            return value;
        }

        public int getCounter() {
            return counter;
        }
    }

    private static class LockFreeStack<T> {
        private AtomicReference<StackNode<T>> head = new AtomicReference<>();
        private AtomicInteger counter = new AtomicInteger(0);

        public void push(T value) {
            StackNode<T> newHead = new StackNode<>(value);
            while (true) {
                StackNode<T> currentHeadNode = head.get();
                newHead.next = currentHeadNode;
                if (head.compareAndSet(currentHeadNode, newHead)) {
                    break;
                } else {
                    LockSupport.parkNanos(1);
                }
            }
            counter.incrementAndGet();
        }

        public T pop() {
            StackNode<T> currentHead = head.get();
            StackNode<T> newHeadNode;
            while (currentHead != null) {
                newHeadNode = currentHead.next;
                if (head.compareAndSet(currentHead, newHeadNode)) {
                    break;
                } else {
                    LockSupport.parkNanos(1);
                    currentHead = head.get();
                }
            }
            counter.incrementAndGet();
            return currentHead != null ? currentHead.value : null;
        }

        public int getCounter() {
            return counter.get();
        }
    }

    private static class StackNode<T> {
        public T value;
        public StackNode<T> next;

        public StackNode(T value) {
            this.value = value;
        }
    }
}
