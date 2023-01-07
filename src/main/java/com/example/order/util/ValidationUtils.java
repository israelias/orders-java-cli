package com.example.order.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Class with utility method for input validation
 */
public class ValidationUtils {
    /**
     * Checks if a string reference is null or empty
     *
     * @param str String to check
     * @return true if the string reference is null or empty, false otherwise
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }

    /**
     * Validates the arguments give to the program
     *
     * @param args Arguments to validate
     * @return A null reference if there are no errors or the error message otherwise
     */
    public static String validateArgs(String[] args) {
        String error;

        // Validate that there's at least one argument
        if (args == null || args.length == 0) {
            error = "The program didn't receive any arguments";
        } else {
            // Validate commands
            error = validateCommand(args[0]);

            // Validate arguments (number and type)
            if (isEmpty(error)) {
                error = validateArgsNumberType(args);
            }
        }

        return error;
    }

    /**
     * Validate if the user entered a valid command
     *
     * @param cmd Command to validate
     * @return Null if the command is valid, error message otherwise
     */
    private static @Nullable String validateCommand(String cmd) {
        for (Commands c : Commands.values()) {
            if (c.getCmd().equals(cmd.toLowerCase())) {
                return null;
            }
        }

        return "Invalid command";
    }

    /**
     * Validates the number and type of the arguments according to the give command
     *
     * @param args Arguments of the command
     * @return Null if the arguments are valid, error message otherwise
     */
    private static String validateArgsNumberType(String @NotNull [] args) {
        String error = null;
        String cmd = args[0].toLowerCase();

        if (Commands.GET.getCmd().equals(cmd)) {
            if (args.length == 2) {
                try {
                    Long.parseLong(args[1]);
                } catch (Exception e) {
                    error = "The second argument must be an integer representing the order ID";
                }
            } else if (args.length == 1) {
                error = "The program didn't receive the ID of the order to get";
            } else {
                error = "The program receive more than one order ID";
            }
        } else if (Commands.UPDATE.getCmd().equals(cmd)) {
            if (args.length == 3) {
                // Validate order id
                try {
                    Long.parseLong(args[1]);
                } catch (Exception e) {
                    error = "The second argument must be an integer representing the order ID";
                }
                // Validate status
                error = validateOrderStatus(args[2]);
            } else if (args.length == 1) {
                error = "The program didn't receive the order ID and the status to update";
            } else {
                error = "The program didn't receive the correct number of arguments";
            }
        } else if (Commands.DELETE.getCmd().equals(cmd)) {
            if (args.length > 1) {
                for (int i = 1; i < args.length; i++) {
                    try {
                        Long.parseLong(args[i]);
                    } catch (Exception e) {
                        error = "The arguments of the command must be integers representing order IDs";
                    }
                }
            } else {
                error = "The program didn't receive the ID(s) of the order(s) to delete";
            }
        } else if (Commands.TOTAL.getCmd().equals(cmd)) {
            if (args.length == 2) {
                try {
                    Integer.parseInt(args[1]);
                } catch (Exception e) {
                    error = "The second argument must be an integer representing the customer ID";
                }
            } else if (args.length == 1) {
                error = "The program didn't receive the customer's ID";
            } else {
                error = "The program receive more than one customer ID";
            }
        }

        return error;
    }

    /**
     * Validate if the user entered a valid order status
     *
     * @param status Status to validate
     * @return Null if the status is valid, error message otherwise
     */
    private static @Nullable String validateOrderStatus(String status) {
        for (OrderStatus os : OrderStatus.values()) {
            if (os.getStatus().equals(status.toLowerCase())) {
                return null;
            }
        }

        return "The third argument must be a valid new order status: " + OrderStatus.listOfValues();
    }
}
