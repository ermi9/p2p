package com.ermiyas.exchange.application.ports;
import com.ermiyas.exchange.domain.orderbook.OrderBook;

public interface OrderBookRepository {

OrderBook findByOutcomeId(long outcomeId);
void save(OrderBook orderBook);    

}
