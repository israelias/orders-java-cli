package com.example.order.service;

import com.example.order.dao.InsertOrderDao;
import com.example.order.dto.ParamsDto;
import com.example.order.util.Database;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * Service class to insert an order
 */
public class InsertOrderService implements OrderService {
    InsertOrderDao insertOrderDao = new InsertOrderDao(Database.getInstance());

    /**
     * Method to execute the service operation
     *
     * @param paramsDTO Object with the parameters to execute the service
     */
    @Override
    public String execute(@NotNull ParamsDto paramsDTO) throws IOException {
        String result;
        long orderId = insertOrderDao.insertOrder(paramsDTO.getOrder());

        if (orderId > 0) {
            result = "A new order with the ID " + orderId + " was inserted";
        } else {
            result = "No order was inserted";
        }

        return result;
    }
}
