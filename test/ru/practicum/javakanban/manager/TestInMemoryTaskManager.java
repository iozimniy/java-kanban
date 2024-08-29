package ru.practicum.javakanban.manager;

import org.junit.jupiter.api.BeforeEach;

public class TestInMemoryTaskManager extends ManagersTest {

    @Override
    @BeforeEach
    public void createTaskManager() {
        taskManager = new InMemoryTaskManager(Managers.getDefaultHistory());
    }
}
