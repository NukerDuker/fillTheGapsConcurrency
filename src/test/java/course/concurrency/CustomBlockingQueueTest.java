package course.concurrency;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


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
        Assertions.assertEquals(10, queue.dequeue());
    }

    @Test
    void singleThreadTest() throws InterruptedException {
        CustomBlockingQueue<Integer> queue = new CustomBlockingQueue<>(10);

        for (int i = 0; i < 9; i++) {
            queue.enqueue(i);
        }

        Assertions.assertEquals(8, queue.dequeue());
        Assertions.assertEquals(7  , queue.dequeue());
    }

}