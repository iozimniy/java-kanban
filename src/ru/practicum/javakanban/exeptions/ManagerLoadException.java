package ru.practicum.javakanban.exeptions;

public class ManagerLoadException extends RuntimeException {
    public ManagerLoadException(final String message) {
        super(message);
    }
}
