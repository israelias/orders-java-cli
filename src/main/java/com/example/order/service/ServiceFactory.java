package com.example.order.service;

import com.example.order.util.Commands;
import org.jetbrains.annotations.NotNull;

/**
 * Factory class to get a service instance
 */
public class ServiceFactory {

    /**
     * Gets the correct service class according to the give command
     *
     * @param cmdEnum Command
     * @return Service class
     */
    public static OrderService get(@NotNull Commands cmdEnum) {
        OrderService service;

        switch (cmdEnum) {
            case GET:
                service = new GetOrderService();
                break;
            case UPDATE:
                service = new UpdateOrderService();
                break;
            case INSERT:
                service = new InsertOrderService();
                break;
            case DELETE:
                service = new DeleteOrderService();
                break;
            case TOTAL:
                service = new TotalOrderService();
                break;
            default:
                throw new RuntimeException("Invalid command received");
        }

        return service;
    }
}
