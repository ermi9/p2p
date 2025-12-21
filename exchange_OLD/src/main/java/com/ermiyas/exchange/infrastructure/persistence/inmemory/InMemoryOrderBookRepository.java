package com.ermiyas.exchange.infrastructure.persistence.inmemory;
import com.ermiyas.exchange.application.ports.OrderBookRepository;
import com.ermiyas.exchange.domain.orderbook.OrderBook;
import java.util.HashMap;
import java.util.Map;
public class InMemoryOrderBookRepository implements OrderBookRepository {
    private final Map<Long,OrderBook> booksByOutcomeId=new HashMap<>();
    @Override
    public OrderBook findByOutcomeId(long outcomeId){
        OrderBook book=booksByOutcomeId.get(outcomeId);
        if(book==null){
            book=new OrderBook(outcomeId);
            booksByOutcomeId.put(outcomeId,book);
        }
        return book;
    }
    @Override
    public void save(OrderBook orderBook){
        booksByOutcomeId.put(orderBook.outcomeId(),orderBook);
    }
}
