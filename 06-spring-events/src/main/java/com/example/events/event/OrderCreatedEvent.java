package com.example.events.event;

/**
 * Custom application event - Simple POJO
 *
 * Modern approach: No need to extend ApplicationEvent
 * Spring 4.2+ supports any object as an event
 */
public class OrderCreatedEvent {
    private final Long orderId;
    private final String customerEmail;
    private final Double amount;

    public OrderCreatedEvent(Long orderId, String customerEmail, Double amount) {
        this.orderId = orderId;
        this.customerEmail = customerEmail;
        this.amount = amount;
        System.out.println("📢 Event created: Order " + orderId);
    }

    public Long getOrderId() {
        return orderId;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public Double getAmount() {
        return amount;
    }
}
