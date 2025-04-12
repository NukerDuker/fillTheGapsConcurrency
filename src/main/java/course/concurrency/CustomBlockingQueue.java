package course.concurrency;

import java.util.LinkedList;
import java.util.List;

public class CustomBlockingQueue<T> {

    private volatile List<T> queue;
    private volatile int size;
    private volatile int quantity = 0;
    private volatile boolean isFull;

    CustomBlockingQueue(int size) {
        queue = new LinkedList<>();
        this.size = size;
    }


    public synchronized void enqueue(T item) throws InterruptedException {
        while (this.isFull) {
            wait();
        }
        this.quantity++;
        this.queue.add(item);
        if (this.quantity == this.size) this.isFull = true;
    }

    public synchronized T dequeue() throws InterruptedException {
        if (!queue.isEmpty()) {
            if (this.isFull) this.isFull = false;
            this.quantity--;
            T removed = queue.remove(quantity);
            notify();
            return removed;
        }
        return null;
    }

    public synchronized int getQuantity() {
        return this.quantity;
    }

}
