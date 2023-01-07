package com.example.order.util;

/**
 * Enum that represents the commands of the application
 */
public enum Commands {
    GET("get"),
    UPDATE("update"),
    DELETE("delete"),
    TOTAL("total"),
    INSERT("insert"),
    HELP("help"),
    EXIT("exit")
    ;

    private final String cmd;

    /**
     * Constructor
     * @param cmd A command of the application
     */
    Commands(final String cmd) {
        this.cmd = cmd;
    }

    /**
     * Getter for the command
     * @return Command as the string
     */
    public String getCmd() {
        return cmd;
    }
}
