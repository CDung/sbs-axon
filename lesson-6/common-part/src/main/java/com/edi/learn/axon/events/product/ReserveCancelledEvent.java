package com.edi.learn.axon.events.product;


import com.edi.learn.axon.domain.OrderId;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

/**
 * Created by Edison Xu on 2017/3/15.
 */
public class ReserveCancelledEvent {

    private OrderId orderId;
    @TargetAggregateIdentifier
    private String productId;
    private int amount;

    public ReserveCancelledEvent() {
    }

    public ReserveCancelledEvent(OrderId orderId, String productId, int amount) {
        this.orderId = orderId;
        this.productId = productId;
        this.amount = amount;
    }

    public OrderId getOrderId() {
        return orderId;
    }

    public String getProductId() {
        return productId;
    }

    public int getAmount() {
        return amount;
    }
}
