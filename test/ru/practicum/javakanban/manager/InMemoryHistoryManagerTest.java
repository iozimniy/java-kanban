package ru.practicum.javakanban.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.javakanban.model.Epic;
import ru.practicum.javakanban.model.Subtask;
import ru.practicum.javakanban.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private TaskManager inMemoryTaskManager;
    private HistoryManager inMemoryHistoryManager;
    private Task task;
    private Epic epic;
    private Subtask subtask;
    private static LocalDateTime TASKS_DATE_TIME = LocalDateTime.of(2024,12,31,12,30);
    private static Duration TASKS_DURATION = Duration.ofMinutes(30);

    @BeforeEach
    public void createInMemoryHistoryManager() {
        inMemoryHistoryManager = Managers.getDefaultHistory();
        inMemoryTaskManager = Managers.getDefault();
    }

    @Test
    public void addTaskItShouldBeInHistoryList() {
        createTestTask();
        inMemoryHistoryManager.add(task);
        var historyTasks = inMemoryHistoryManager.getHistory();
        assertAll(
                () -> assertEquals(1, historyTasks.size(), "При добавлении задачи historyList пуст"),
                () -> assertEquals(task, historyTasks.getFirst(), "Задача не добавилась в historyList")
        );
    }

    @Test
    public void addEpicItShouldBeInHistoryList() {
        createTestEpic();
        inMemoryHistoryManager.add(epic);
        var historyTasks = inMemoryHistoryManager.getHistory();
        assertAll(
                () -> assertEquals(1, historyTasks.size(), "При добавлении эпика historyList пуст"),
                () -> assertEquals(epic, historyTasks.getFirst(), "Эпик не добавился в historyList")
        );
    }

    @Test
    public void addSubtaskItShouldBeInHistoryList() {
        createTestSubtask();
        inMemoryHistoryManager.add(subtask);
        var historyTasks = inMemoryHistoryManager.getHistory();
        assertAll(
                () -> assertEquals(1, historyTasks.size(), "При добавлении подзадачи historyList пуст"),
                () -> assertEquals(subtask, historyTasks.getFirst(), "Подзадача не добавилась в historyList")
        );
    }

    @Test
    public void removeTaskItShouldNotBeInHistoryList() {
        createTestTask();
        inMemoryHistoryManager.add(task);
        inMemoryHistoryManager.remove(task.getId());
        var historyTasks = inMemoryHistoryManager.getHistory();
        assertTrue(historyTasks.isEmpty(), "Задача не удалилась из historyList");
    }

    @Test
    public void removeEpicItShouldNotBeInHistoryList() {
        createTestEpic();
        inMemoryHistoryManager.add(epic);
        inMemoryHistoryManager.remove(epic.getId());
        var historyTasks = inMemoryHistoryManager.getHistory();
        assertTrue(historyTasks.isEmpty(), "Эпик не удалился из historyList");
    }


    @Test
    public void removeSubtaskItShouldNotBeInHistoryList() {
        createTestSubtask();
        inMemoryHistoryManager.add(subtask);
        inMemoryHistoryManager.remove(subtask.getId());
        var historyTasks = inMemoryHistoryManager.getHistory();
        assertTrue(historyTasks.isEmpty(), "Подзадача не удалилась из historyList");
    }


    @Test
    public void addTwoTheSameTasksFirstShouldBeRemoved() {
        createTestTask();
        inMemoryHistoryManager.add(task);
        inMemoryHistoryManager.add(task);
        var historyTasks = inMemoryHistoryManager.getHistory();
        assertAll(
                () -> assertEquals(1, historyTasks.size(), "В historyList лишние задачи"),
                () -> assertTrue(historyTasks.contains(task), "Задача куда-то подевалась после двойного " +
                        "добавления")
        );
    }

    @Test
    public void addTwoTheSameSubtasksEpicAndSubtaskInHistory() {
        createTestSubtask();
        inMemoryHistoryManager.add(epic);
        inMemoryHistoryManager.add(subtask);
        inMemoryHistoryManager.add(subtask);
        var historyTasks = inMemoryHistoryManager.getHistory();
        assertAll(
                () -> assertEquals(2, historyTasks.size(), "В historyList неожиданное количество " +
                        "объектов"),
                () -> assertTrue(historyTasks.contains(epic), "В historyList не хватает эпика"),
                () -> assertTrue(historyTasks.contains(subtask), "В historyList не хватает подзадачи")
        );
    }

    @Test
    public void addEpicSubtaskAndTaskAndTheSameSubtaskIsFirstInHistory() {
        createTestSubtask();
        createTestTask();
        inMemoryHistoryManager.add(epic);
        inMemoryHistoryManager.add(subtask);
        inMemoryHistoryManager.add(task);
        inMemoryHistoryManager.add(subtask);
        var historyTasks = inMemoryHistoryManager.getHistory();
        assertAll(
                () -> assertEquals(subtask, historyTasks.getFirst(), "Последняя просмотренная задача не в " +
                        "начале списка"),
                () -> assertEquals(3, historyTasks.size(), "Список имеет неожиданный размер")
        );

    }

    //вспомогательные методы
    private void createTestTask() {
        task = new Task("Задача", "Описание задачи", TASKS_DURATION, TASKS_DATE_TIME);
        inMemoryTaskManager.createTask(task);
    }

    private void createTestEpic() {
        epic = new Epic("Эпик", "Описание эпика");
        inMemoryTaskManager.createEpic(epic);
    }

    private void createTestSubtask() {
        epic = new Epic("Эпик", "Описание эпика");
        inMemoryTaskManager.createEpic(epic);
        subtask = new Subtask("Подзадача", "Описание подзадачи", TASKS_DURATION, TASKS_DATE_TIME);
        inMemoryTaskManager.createSubtask(subtask, epic.getId());
    }
}