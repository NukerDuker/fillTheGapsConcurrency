package course.concurrency.m2_async.minPrice;


import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class PriceAggregator {

    private PriceRetriever priceRetriever = new PriceRetriever();
    private ExecutorService executor = Executors.newFixedThreadPool(20);

    public void setPriceRetriever(PriceRetriever priceRetriever) {
        this.priceRetriever = priceRetriever;
    }

    private Collection<Long> shopIds = Set.of(10l, 45l, 66l, 345l, 234l, 333l, 67l, 123l, 768l);

    public void setShops(Collection<Long> shopIds) {
        this.shopIds = shopIds;
    }

    public double getMinPrice(long itemId) {
        List<CompletableFuture<Double>> cf = shopIds.stream().map(shopId ->
            CompletableFuture.supplyAsync(() -> priceRetriever.getPrice(itemId, shopId), executor)
                    .completeOnTimeout(Double.POSITIVE_INFINITY, 2900, TimeUnit.MILLISECONDS)
                    .exceptionally(ex -> Double.POSITIVE_INFINITY))
                    .toList();

        CompletableFuture.allOf(cf.toArray(new CompletableFuture[0])).join();

        return cf
                .stream()
                .mapToDouble(CompletableFuture::join)
                .filter(Double::isFinite).min()
                .orElse(Double.NaN);
    }
}
