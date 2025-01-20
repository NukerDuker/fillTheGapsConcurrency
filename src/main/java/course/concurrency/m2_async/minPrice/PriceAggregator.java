package course.concurrency.m2_async.minPrice;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class PriceAggregator {

    private PriceRetriever priceRetriever = new PriceRetriever();
//    private ExecutorService executor = Executors.newFixedThreadPool(20);

    public void setPriceRetriever(PriceRetriever priceRetriever) {
        this.priceRetriever = priceRetriever;
    }

    private Collection<Long> shopIds = Set.of(10l, 45l, 66l, 345l, 234l, 333l, 67l, 123l, 768l);

    public void setShops(Collection<Long> shopIds) {
        this.shopIds = shopIds;
    }

    public double getMinPrice(long itemId) {
        List<Double> prices = new ArrayList<>();
        List<CompletableFuture<Void>> completableFutures = new ArrayList<>();

        ExecutorService ex = Executors.newCachedThreadPool();
        System.out.println("shops : " + shopIds.size());
        IntStream.range(0, shopIds.size()).boxed().forEach(shopId -> {
            System.out.println("inside " + shopId);
            CompletableFuture<Void> getMinimumPriceFuture = CompletableFuture
                    .supplyAsync(() -> priceRetriever.getPrice(itemId, shopId), ex)
                    .thenAccept(price -> {
                        System.out.println("inside 1");
                        synchronized (prices) {
                            System.out.println("inside 2");
                            prices.add(price);
                            System.out.println(prices);
                        }
                    }).exceptionally((exception) -> {
                        System.out.println("EXCEPTION: " + exception.getMessage());
                        exception.printStackTrace();
                        return null;
                    });
            completableFutures.add(getMinimumPriceFuture);

        });
//        System.out.println("threads size: " + threadPoolExecutor.getPoolSize());
//        System.out.println("Queue size: " + threadPoolExecutor.getQueue().size());
//        System.out.println("threads size: " + threadPoolExecutor.getPoolSize());
//        System.out.println("Queue size: " + threadPoolExecutor.getQueue().size());
        try {
            Thread.sleep(2700);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        synchronized (prices) {
            System.out.println("result: " + prices);
            ex.shutdown();
            return prices.stream().min(Double::compareTo).orElse(Double.NaN);
        }
    }
}
