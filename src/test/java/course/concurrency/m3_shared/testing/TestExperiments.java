package course.concurrency.m3_shared.testing;

import org.junit.jupiter.api.RepeatedTest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestExperiments {

    // Don't change this class
    public static class Counter {
        private volatile int counter = 0;

        public void increment() {
            counter++;
        }

        public int get() {
            return counter;
        }
    }

    @RepeatedTest(1000)
    public void counterShouldFail() {
        int threadCount = Runtime.getRuntime().availableProcessors()*3;
        int iterations = 200;

        Counter counter = new Counter();
        try (ExecutorService executor = Executors.newFixedThreadPool(threadCount)) {
            for (int i = 0; i < iterations; i++) {
                executor.submit(() -> {
                    counter.increment();
                });
            }
        }
        assertEquals(iterations, counter.get());
    }
}
