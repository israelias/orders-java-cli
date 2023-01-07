package com.example.order.service;

import com.example.order.dao.TotalOrderDao;
import com.example.order.dto.ParamsDto;
import com.example.order.util.Database;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

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
    public String execute(ParamsDto paramsDTO) throws IOException, SQLException {
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
