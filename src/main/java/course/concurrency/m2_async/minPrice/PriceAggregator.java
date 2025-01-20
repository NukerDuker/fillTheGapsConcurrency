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
        ExecutorService ex = Executors.newCachedThreadPool();
        IntStream.range(0, shopIds.size()).boxed().forEach(shopId -> {
            CompletableFuture
                    .supplyAsync(() -> priceRetriever.getPrice(itemId, shopId), ex)
                    .thenAccept(price -> {
                        synchronized (prices) {
                            prices.add(price);
                        }
                    }).exceptionally((exception) -> null);
        });
        try {
            Thread.sleep(2700);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        synchronized (prices) {
            ex.shutdown();
            return prices.stream().min(Double::compareTo).orElse(Double.NaN);
        }
    }
}
