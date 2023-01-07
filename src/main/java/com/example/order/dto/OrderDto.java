package com.example.order.dto;

import java.util.Date;
import java.util.List;

/**
 * DTO class to insert/get an order
 */
public class OrderDto {

    private long orderId;

    private long customerId;

    private Date date;

    private String status;

    private List<OrderDetailDto> orderDetail;

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<OrderDetailDto> getOrderDetail() {
        return orderDetail;
    }

    public void setOrderDetail(List<OrderDetailDto> orderDetail) {
        this.orderDetail = orderDetail;
    }

    @Override
    public String toString() {
        return "OrderDTO{" +
                "orderId=" + orderId +
                ", customerId=" + customerId +
                ", date=" + date +
                ", status='" + status + '\'' +
                ", orderDetail=" + orderDetail +
                '}';
    }
}
