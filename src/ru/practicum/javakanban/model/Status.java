package ru.practicum.javakanban.model;

public enum Status {
    NEW("NEW"),
    IN_PROGRESS("IN_PROGRESS"),
    DONE("DONE");

    private String stringValue;

    Status(String stringValue) {
        this.stringValue = stringValue;
    }

    public static Status fromString(String stringValue) {
        if (stringValue != null) {
            for (Status status : Status.values()) {
                if (stringValue.equals(status.stringValue)) {
                    return status;
                }
            }
        }

        throw new IllegalArgumentException("Такого статуса не существует.");
    }
}