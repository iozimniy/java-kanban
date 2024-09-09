package ru.practicum.javakanban.manager;

import org.junit.jupiter.api.BeforeEach;

public class InMemoryTaskManagerTest extends ManagersTest {

    @Override
    @BeforeEach
    public void createTaskManager() {
        taskManager = new InMemoryTaskManager(Managers.getDefaultHistory());
    }
}
