package course.concurrency.m3_shared;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class Counter {

    private static final Object lock = new Object();
    private static final AtomicInteger counter = new AtomicInteger(0);

    public static void first() {
        synchronized (lock) {
            while (counter.get() != 1) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            System.out.println(counter.get());
            counter.set(2);
            lock.notifyAll();
        }
    }

    public static void second() {
        synchronized (lock) {
            while (counter.get() != 2) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            System.out.println(counter.get());
            counter.set(3);
            lock.notifyAll();
        }
    }

    public static void third() {
        synchronized (lock) {
            while (counter.get() != 3) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            System.out.println(counter);
            counter.set(1);
            lock.notifyAll();
        }
    }

    public static void main(String[] args) {
        counter.set(1);
        try (ExecutorService executor = Executors.newCachedThreadPool()) {
            IntStream.range(0, 5).forEach(i -> executor.submit(() -> {
                Thread t1 = new Thread(Counter::first);
                Thread t2 = new Thread(Counter::second);
                Thread t3 = new Thread(Counter::third);
                t1.start();
                t2.start();
                t3.start();
            }));
        }
    }
}
