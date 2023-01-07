package com.example.order;

import com.example.order.dto.OrderDto;
import com.example.order.dto.OrderDetailDto;
import com.example.order.dto.ParamsDto;
import com.example.order.service.OrderService;
import com.example.order.service.ServiceFactory;
import com.example.order.util.Commands;
import com.example.order.util.OrderStatus;
import com.example.order.util.ValidationUtils;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static com.example.order.util.ValidationUtils.*;

/**
 * Main class (point of entry) of the application
 */
public class Main {
    /**
     * Prompt to enter a command
     */
    private static final String INITIAL_PROMPT = "Enter command: ";

    /**
     * Main method
     *
     * @param args Command and arguments to be interpreted by the application
     */
    public static void main(String[] args) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String command = "";

        try {
            while (!Commands.EXIT.getCmd().equalsIgnoreCase(command)) {
                System.out.print(INITIAL_PROMPT);
                String line = reader.readLine();
                String[] tokens = line.split(" ");

                if (tokens.length > 0) {
                    command = tokens[0];

                    if (!Commands.EXIT.getCmd().equalsIgnoreCase(command)) {
                        processCommand(tokens);
                    }
                } else {
                    System.out.println("Invalid command, try again.");
                    command = "";
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Process the commands of the application
     *
     * @param args Arguments given to the application
     */
    private static void processCommand(String[] args) {
        String error = validateArgs(args);

        if (isEmpty(error)) {
            Commands cmdEnum = Commands.valueOf(args[0].toUpperCase());

            if (cmdEnum.equals(Commands.HELP)) {
                displayHelp(null);
            } else {
                OrderService service = ServiceFactory.get(cmdEnum);

                // Get parameters for the service from arguments (or ask for them in the case of inserting)
                ParamsDto paramsDTO = getParameters(cmdEnum, args);
                System.out.println(service.execute(paramsDTO));
            }
        } else {
            displayHelp(error);
        }
    }

    /**
     * Given a command (ex. get), validate the arguments give to the program
     *
     * @param cmdEnum Command
     * @param args    Arguments of the command
     * @return Object with the information (arguments) to be processed
     */
    private static @NotNull ParamsDto getParameters(Commands cmdEnum, String[] args) {
        ParamsDto paramsDTO = new ParamsDto();

        if (cmdEnum == Commands.GET) {
            paramsDTO.setOrderId(Long.parseLong(args[1]));
        } else if (cmdEnum == Commands.UPDATE) {
            paramsDTO.setOrderId(Long.parseLong(args[1]));
            paramsDTO.setStatus(args[2]);
        } else if (cmdEnum == Commands.DELETE) {
            List<Long> orderIds = new ArrayList<>();
            for (int i = 1; i < args.length; i++) {
                orderIds.add(Long.parseLong(args[i]));
            }
            paramsDTO.setOrderIds(orderIds);
        } else if (cmdEnum == Commands.TOTAL) {
            paramsDTO.setCustomerId(Long.parseLong(args[1]));
        } else {
            paramsDTO.setOrder(askForOrderDetails());
        }

        return paramsDTO;
    }

    /**
     * Method that ask for the information to insert an order
     *
     * @return Object with the information to insert an order
     */
    private static @NotNull OrderDto askForOrderDetails() {
        OrderDto orderDTO = new OrderDto();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        boolean invalidInput;
        try {
            /* Ask for customer ID */
            do {
                System.out.print("Customer ID: ");
                String line = reader.readLine();
                try {
                    orderDTO.setCustomerId(Long.parseLong(line));
                    invalidInput = false;
                } catch (Exception e) {
                    invalidInput = true;
                }
            } while (invalidInput);

            boolean insertMoreProducts = true;
            List<OrderDetailDto> list = new ArrayList<>();
            do {
                OrderDetailDto orderDetailDTO = new OrderDetailDto();

                // Ask for product ID
                do {
                    System.out.print("Product ID: ");
                    String line = reader.readLine();
                    try {
                        orderDetailDTO.setProductId(Long.parseLong(line));
                        invalidInput = false;
                    } catch (Exception e) {
                        invalidInput = true;
                    }
                } while (invalidInput);

                /* Ask for product quantity */
                do {
                    System.out.print("Quantity: ");
                    String line = reader.readLine();
                    try {
                        orderDetailDTO.setQuantity(Integer.parseInt(line));
                        invalidInput = false;
                    } catch (Exception e) {
                        invalidInput = true;
                    }
                } while (invalidInput);
                list.add(orderDetailDTO);

                /* Ask to insert more products */
                do {
                    System.out.print("Insert more products? (y/n): ");
                    String line = reader.readLine();
                    if (isEmpty(line)) {
                        invalidInput = true;
                    } else if ("y".equalsIgnoreCase(line)) {
                        insertMoreProducts = true;
                        invalidInput = false;
                    } else if ("n".equalsIgnoreCase(line)) {
                        insertMoreProducts = false;
                        invalidInput = false;
                    } else {
                        invalidInput = true;
                    }
                } while (invalidInput);

            } while (insertMoreProducts);

            orderDTO.setOrderDetail(list);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        return orderDTO;
    }

    /**
     * Method that display information about how to use the application
     *
     * @param error Error message to display
     */
    private static void displayHelp(String error) {
        if (!ValidationUtils.isEmpty(error)) {
            System.err.println("Error: " + error);
        }
        System.out.println("Usage: " + INITIAL_PROMPT + " <command> [<args>]");
        System.out.println("get      Displays information about an order. It takes the ID of the order to display as argument. Ex: get 1");
        System.out.println("update   Updates the status of an order. It takes two arguments, the ID of the order to update and the status (" + OrderStatus.listOfValues() + "). Ex: update 1 PAID");
        System.out.println("delete   Deletes an order. It takes a variable number of arguments, representing the IDs of the orders to delete. Ex: delete 20 35 41");
        System.out.println("insert   Inserts a new order. It doesn't take more arguments, however, the application will prompt for all the information of the new order after issuing this command.");
        System.out.println("total    Gives the total amount of all paid orders of a customer. It takes the ID of the customer as argument. Ex: total 2");
        System.out.println("exit     Exits the application");
        System.out.println("help     Displays usage instructions");
    }
}
