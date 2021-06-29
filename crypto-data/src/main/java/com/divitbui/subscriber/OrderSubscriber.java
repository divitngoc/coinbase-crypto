package com.divitbui.subscriber;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.stream.Stream;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.stereotype.Service;

import com.divitbui.config.OrderConfig;
import com.divitbui.model.OrderBook;
import com.divitbui.model.response.L2UpdateResponse;
import com.divitbui.model.response.SnapshotResponse;
import com.divitbui.provider.OrderBookProvider;
import com.divitbui.util.PrintUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderSubscriber implements Subscriber<JsonNode> {

    private final ObjectMapper mapper;
    private final OrderBookProvider orderBookProvider;
    private final OrderConfig orderConfig;

    @Override
    public void onSubscribe(final Subscription s) {
        s.request(Long.MAX_VALUE);
    }

    @Override
    public void onNext(final JsonNode t) {
        final String type = t.get("type").asText();

        try {
            switch (type) {
                case "snapshot":
                    handleSnapshot(mapper.readerFor(SnapshotResponse.class).readValue(t));
                    PrintUtils.printStatus(orderBookProvider.getOrderBookBySymbol(t.get("product_id").textValue()), orderConfig.getMaxPrintLevel());
                    break;
                case "l2update":
                    handleL2Update(mapper.readerFor(L2UpdateResponse.class).readValue(t));
                    PrintUtils.printStatus(orderBookProvider.getOrderBookBySymbol(t.get("product_id").textValue()), orderConfig.getMaxPrintLevel());
                    break;
                case "subscriptions":
                    log.debug("Successful subscription response", t.toPrettyString());
                    break;
                default:
                    log.error("Unhandled type [{}], skipping...", type);
            }
        } catch (final IOException e) {
            log.error("Unable to convert JSON {}...", e.getMessage(), e);
        }
    }

    private void handleSnapshot(final SnapshotResponse snapshot) {
        final OrderBook orderBook = orderBookProvider.getOrderBookBySymbol(snapshot.getProductId());
        // snapshot's bids and asks are already sorted by levels asc
        Stream.concat(snapshot.getBids()
                              .stream()
                              .limit(orderConfig.getMaxPrintLevel()),
                      snapshot.getAsks()
                              .stream()
                              .limit(orderConfig.getMaxPrintLevel()))
              .forEach(orderPosition -> orderBook.getOrders(orderPosition.getSide()).put(orderPosition.getPrice(), orderPosition.getSize()));
    }

    private void handleL2Update(final L2UpdateResponse updateResponse) {
        updateResponse.getChanges()
                      .forEach(orderPosition -> {
                          ConcurrentNavigableMap<BigDecimal, BigDecimal> orders = orderBookProvider.getOrderBookBySymbol(updateResponse.getProductId())
                                                                                                   .getOrders(orderPosition.getSide());

                          if (orderPosition.getSize().compareTo(BigDecimal.ZERO) == 0) {
                              log.debug("Removing {} price level [{}]", orderPosition.getSide(), orderPosition.getPrice().toPlainString());
                              orders.remove(orderPosition.getPrice());
                          } else {
                              orders.compute(orderPosition.getPrice(), (k, v) -> {
                                  log.debug("Updating position size at {} price level [{}] from [{}] to [{}]",
                                            orderPosition.getSide(),
                                            k.toPlainString(),
                                            v == null ? BigDecimal.ZERO : v.toPlainString(),
                                            orderPosition.getSize().toPlainString());
                                  return orderPosition.getSize();
                              });
                          }
                      });
    }

    @Override
    public void onError(final Throwable t) {
        log.error("Error {}", t.getMessage(), t);
    }

    @Override
    public void onComplete() {}

}
