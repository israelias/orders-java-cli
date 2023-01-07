package com.example.order.service;

import com.example.order.dao.DeleteOrderDao;
import com.example.order.dto.ParamsDto;
import com.example.order.util.Database;

/**
 * Service class to delete one or more orders
 */
public class DeleteOrderService implements OrderService {
    private final DeleteOrderDao deleteOrderDao = new DeleteOrderDao(Database.getInstance());

    /**
     * Method to execute the service operation
     *
     * @param paramsDTO Object with the parameters to execute the service
     */
    @Override
    public String execute(ParamsDto paramsDTO) {
        String result;
        int rowsAffected = deleteOrderDao.deleteOrdersById(paramsDTO);

        if (rowsAffected <= 0) {
            result = "No rows affected";
        } else {
            result = "Rows affected: " + rowsAffected;
        }

        return result;
    }
}
