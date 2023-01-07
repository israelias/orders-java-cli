package com.example.order.util;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Enum that represents the status of an order
 */
public enum OrderStatus {
    CREATED("created"),
    PAID("paid"),
    CANCELED("canceled");

    private final String status;

    /**
     * Constructor
     *
     * @param status An order status
     */
    OrderStatus(final String status) {
        this.status = status;
    }

    /**
     * Getter for the status
     *
     * @return Status as the string
     */
    public String getStatus() {
        return status;
    }

    /**
     * Method to get all the status values of this Enum as a comma-separated string
     *
     * @return All the status values as a comma-separated string
     */
    public static @NotNull String listOfValues() {
        return Arrays.stream(OrderStatus.values())
                .map(Enum::toString)
                .collect(Collectors.joining(",")).toLowerCase();
    }
}
