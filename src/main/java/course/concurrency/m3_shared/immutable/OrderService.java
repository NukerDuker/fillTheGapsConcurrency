package course.concurrency.m3_shared.immutable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderService {

    private Map<Long, OrderImmutable> currentOrders = new HashMap<>();
    private long nextId = 0L;

    private synchronized long nextId() {
        return nextId++;
    }

    public synchronized long createOrder(List<Item> items) {
        long id = nextId();
        OrderImmutable orderImmutable = new OrderImmutable(items);
        currentOrders.put(id, orderImmutable);
        return id;
    }

    public synchronized void updatePaymentInfo(long orderId, PaymentInfo paymentInfo) {
        currentOrders.get(orderId).withPaymentInfo(paymentInfo);
        if (currentOrders.get(orderId).checkStatus()) {
            deliver(currentOrders.get(orderId));
        }
    }

    public synchronized void setPacked(long orderId) {
        currentOrders.get(orderId).packed();
        if (currentOrders.get(orderId).checkStatus()) {
            deliver(currentOrders.get(orderId));
        }
    }

    private synchronized void deliver(OrderImmutable orderImmutable) {
        /* ... */
        currentOrders.get(orderImmutable.getId()).withStatus(OrderImmutable.Status.DELIVERED);
    }

    public synchronized boolean isDelivered(long orderId) {
        return currentOrders.get(orderId).getStatus().equals(OrderImmutable.Status.DELIVERED);
    }
}
