package course.concurrency;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

import static org.junit.jupiter.api.Assertions.*;


class CustomBlockingQueueTest {

    @Test
    void testCustomBlockingQueue() throws InterruptedException {
        CustomBlockingQueue<Integer> queue = new CustomBlockingQueue<>(10);
        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(11);
        for (int i = 0; i < 11; i++) {
            final int index = i;
            executor.submit(() -> {
                try {
                    queue.enqueue(index);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            latch.countDown();
        }
        latch.await();
        assertEquals(10, queue.dequeue());
    }

    @Test
    void singleThreadTest() throws InterruptedException {
        CustomBlockingQueue<Integer> queue = new CustomBlockingQueue<>(10);

        for (int i = 0; i < 10; i++) {
            queue.enqueue(i);
        }
        for (int i = 0; i < 10; i++) {
            assertEquals(i, queue.dequeue());
        }
        assertNull(queue.dequeue());
    }

    @Test
    void multiThreadTest() {
        CustomBlockingQueue<Integer> queue = new CustomBlockingQueue<>(10);
        try (ExecutorService executor = Executors.newFixedThreadPool(15)) {
            for (int i = 0; i < 10; i++) {
                int finalI = i;
                executor.execute(() -> {
                    try {
                        queue.enqueue(finalI);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                });
            }
            for (int i = 0; i < 10; i++) {
                assertNotNull(queue.dequeue());
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void multiThreadOversizeQueue() throws InterruptedException {
        CustomBlockingQueue<Integer> queue = new CustomBlockingQueue<>(10);
        List<Integer> list = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            queue.enqueue(i);
            list.add(i);
        }
        CountDownLatch latch = new CountDownLatch(1);
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            try {
                queue.enqueue(10);
                latch.countDown();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        assertEquals(10, queue.getQuantity());
        queue.dequeue();
        latch.await();
        assertEquals(10, queue.getQuantity());
        ExecutorService executor = Executors.newFixedThreadPool(10);
        List<Integer> finalList = new LinkedList<>();
        CountDownLatch latch2 = new CountDownLatch(10);
        for (int i = 0; i < 10; i++) {
            int finalI = i;
            executor.execute(() -> {
                try {
                    synchronized (finalList) {
                        int r = queue.dequeue();
                        finalList.add(finalI);
                        latch2.countDown();
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        latch2.await();
        for (int i = 0; i < 10; i++) {
            assertEquals(i, finalList.removeFirst());
        }
    }

}