package ru.practicum.javakanban.exeptions;

public class ManagerSaveException extends RuntimeException {
    public ManagerSaveException(final String message) {
        super(message);
    }
}
