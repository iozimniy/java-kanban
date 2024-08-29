package ru.practicum.javakanban.exeptions;

public class ManagerPrioritizeException extends RuntimeException {
    public ManagerPrioritizeException(final String message) {
        super(message);
    }
}
