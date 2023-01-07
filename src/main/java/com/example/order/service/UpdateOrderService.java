package com.example.order.service;

import com.example.order.dao.UpdateOrderDao;
import com.example.order.dto.ParamsDto;
import com.example.order.util.Database;

/**
 * Service class to update an order
 */
public class UpdateOrderService implements OrderService {
    private final UpdateOrderDao updateOrderDao = new UpdateOrderDao(Database.getInstance());

    /**
     * Method to execute the service operation
     *
     * @param paramsDTO Object with the parameters to execute the service
     */
    @Override
    public String execute(ParamsDto paramsDTO) {
        String result;
        int rowsAffected = updateOrderDao.updateOrderStatus(paramsDTO);

        if (rowsAffected <= 0) {
            result = "No rows affected";
        } else {
            result = "Rows affected: " + rowsAffected;
        }

        return result;
    }
}
