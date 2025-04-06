package course.concurrency;

import java.util.LinkedList;
import java.util.List;

public class CustomBlockingQueue<T> {

    private volatile List<T> queue;
    private volatile int size;
    private volatile int lastIndex = 0;
    private volatile boolean isFull;

    CustomBlockingQueue(int size) {
        queue = new LinkedList<>();
        this.size = size;
    }


    public synchronized void enqueue(T item) throws InterruptedException {
        if (this.isFull) {
            this.wait();
        } else {
            this.lastIndex++;
            this.queue.add(item);
            if (this.lastIndex == this.size - 1) this.isFull = true;
        }
    }

    public synchronized T dequeue() throws InterruptedException {
        if (!queue.isEmpty()) {
            this.notify();
            if (this.isFull) this.isFull = false;
            this.lastIndex--;
            return queue.remove(lastIndex);
        }
        return null;
    }

}
