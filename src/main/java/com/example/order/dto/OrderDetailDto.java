package com.example.order.dto;

/**
 * DTO class to insert the details of an order
 */
public class OrderDetailDto {

    private long orderId;

    private long productId;

    private int quantity;

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "OrderDetailDTO{" +
                "orderId=" + orderId +
                ", productId=" + productId +
                ", quantity=" + quantity +
                '}';
    }
}
