package ru.practicum.javakanban.exeptions;

public class NotFoundException extends Exception {
    public NotFoundException(final String message) {
        super(message);
    }
}
