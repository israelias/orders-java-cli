package com.example.order.dto;

import java.util.List;

/**
 * DTO class for service parameters
 */
public class ParamsDto {

    private long orderId;

    private String status;

    private long customerId;

    private List<Long> orderIds;

    private OrderDto order;

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public List<Long> getOrderIds() {
        return orderIds;
    }

    public void setOrderIds(List<Long> orderIds) {
        this.orderIds = orderIds;
    }

    public OrderDto getOrder() {
        return order;
    }

    public void setOrder(OrderDto order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return "ParamsDTO{" +
                "orderId=" + orderId +
                ", status='" + status + '\'' +
                ", customerId=" + customerId +
                ", orderIds=" + orderIds +
                ", order=" + order +
                '}';
    }
}
