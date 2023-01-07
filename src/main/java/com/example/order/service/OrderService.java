package com.example.order.service;

import com.example.order.dto.ParamsDto;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Interface for service classes
 */
public interface OrderService {
    String execute(ParamsDto paramsDTO) throws IOException, SQLException;
}
