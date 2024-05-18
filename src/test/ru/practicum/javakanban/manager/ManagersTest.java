package ru.practicum.javakanban.manager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void getDefaultReturnsObjectTaskManager() {
        assertInstanceOf(TaskManager.class, Managers.getDefault(), "Объект должен иметь тип TaskManager");
    }

    @Test
    void getDefaultHistoryRetunsObjectHistoryManager() {
        assertInstanceOf(HistoryManager.class, Managers.getDefaultHistory(), "Объект должен иметь тип HistoryManager");
    }
}