package com.example.order.service;

import com.example.order.dao.TotalOrderDao;
import com.example.order.dto.ParamsDto;
import com.example.order.util.Database;

import java.math.BigDecimal;

/**
 * Service class to get the total of the orders of a customer
 */
public class TotalOrderService implements OrderService {
    private final TotalOrderDao totalOrderDao = new TotalOrderDao(Database.getInstance());

    /**
     * Method to execute the service operation
     *
     * @param paramsDTO Object with the parameters to execute the service
     */
    @Override
    public String execute(ParamsDto paramsDTO) {
        String result;
        BigDecimal total = totalOrderDao.getTotalAllPaidOrders(paramsDTO);

        if (total != null) {
            result = "Total: " + total.toString();
        } else {
            result = "No paid orders for the customer with ID " + paramsDTO.getCustomerId() + " found";
        }
        return result;
    }
}
