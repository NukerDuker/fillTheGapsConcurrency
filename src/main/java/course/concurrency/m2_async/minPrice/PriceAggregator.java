package course.concurrency.m2_async.minPrice;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class PriceAggregator {

    private PriceRetriever priceRetriever = new PriceRetriever();

    public void setPriceRetriever(PriceRetriever priceRetriever) {
        this.priceRetriever = priceRetriever;
    }

    private Collection<Long> shopIds = Set.of(10l, 45l, 66l, 345l, 234l, 333l, 67l, 123l, 768l);

    public void setShops(Collection<Long> shopIds) {
        this.shopIds = shopIds;
    }

    public double getMinPrice(long itemId) {
        List<Double> prices = new ArrayList<>();
        IntStream.range(0, shopIds.size()).boxed().forEach(shopId -> {
            System.out.println("inside 0");
            CompletableFuture<Double> getMinimumPriceFuture = CompletableFuture.supplyAsync(() -> priceRetriever.getPrice(1L, shopId))
                    .thenApplyAsync(price -> {
                        System.out.println("inside 1");
                        synchronized (prices) {
                            System.out.println("inside 2");
                            prices.add(price);
                            System.out.println(prices);
                        }
                        return price;
                    });
        });
        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        synchronized (prices) {
            System.out.println(prices);
            if (prices.isEmpty()) return Double.NaN;
            else return prices.stream().min(Double::compareTo).get();
        }
    }
}
