package ru.practicum.javakanban.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.javakanban.model.Epic;
import ru.practicum.javakanban.model.Subtask;
import ru.practicum.javakanban.model.Task;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private TaskManager inMemoryTaskManager;
    private HistoryManager inMemoryHistoryManager;
    private Task task;
    private Epic epic;
    private Subtask subtask;

    @BeforeEach
    public void createInMemoryHistoryManager() {
        inMemoryHistoryManager = Managers.getDefaultHistory();
        inMemoryTaskManager = Managers.getDefault();
    }

    @Test
    void addTaskItShouldBeInHistoryList() {
        createTestTask();
        inMemoryHistoryManager.add(task);
        var historyTasks = inMemoryHistoryManager.getHistory();
        assertAll(
                () -> assertEquals(1, historyTasks.size(), "При добавлении задачи historyList пуст"),
                () -> assertEquals(task, historyTasks.getFirst(), "Задача не добавилась в historyList")
        );
    }

    @Test
    void addEpicItShouldBeInHistoryList() {
        createTestEpic();
        inMemoryHistoryManager.add(epic);
        var historyTasks = inMemoryHistoryManager.getHistory();
        assertAll(
                () -> assertEquals(1, historyTasks.size(), "При добавлении эпика historyList пуст"),
                () -> assertEquals(epic, historyTasks.getFirst(), "Эпик не добавился в historyList")
        );
    }

    @Test
    void addSubtaskItShouldBeInHistoryList() {
        createTestSubtask();
        inMemoryHistoryManager.add(subtask);
        var historyTasks = inMemoryHistoryManager.getHistory();
        assertAll(
                () -> assertEquals(1, historyTasks.size(), "При добавлении подзадачи historyList пуст"),
                () -> assertEquals(subtask, historyTasks.getFirst(), "Подзадача не добавилась в historyList")
        );
    }

    @Test
    void removeTaskItShouldNotBeInHistoryList() {
        createTestTask();
        inMemoryHistoryManager.add(task);
        inMemoryHistoryManager.remove(task.getId());
        var historyTasks = inMemoryHistoryManager.getHistory();
        assertTrue(historyTasks.isEmpty(), "Задача не удалилась из historyList");
    }

    @Test
    void removeEpicItShouldNotBeInHistoryList() {
        createTestEpic();
        inMemoryHistoryManager.add(epic);
        inMemoryHistoryManager.remove(epic.getId());
        var historyTasks = inMemoryHistoryManager.getHistory();
        assertTrue(historyTasks.isEmpty(), "Эпик не удалился из historyList");
    }

    @Test
    void removeSubtaskItShouldNotBeInHistoryList() {
        createTestSubtask();
        inMemoryHistoryManager.add(subtask);
        inMemoryHistoryManager.remove(subtask.getId());
        var historyTasks = inMemoryHistoryManager.getHistory();
        assertTrue(historyTasks.isEmpty(), "Подзадача не удалилась из historyList");
    }

    @Test
    void addTwoTheSameTasksFirstShouldBeRemoved() {
        createTestTask();
        inMemoryHistoryManager.add(task);
        inMemoryHistoryManager.add(task);
        var historyTasks = inMemoryHistoryManager.getHistory();
        assertAll(
                () -> assertTrue(historyTasks.size() == 1, "В historyList лишние задачи"),
                () -> assertTrue(historyTasks.contains(task), "Задача куда-то подевалась после двойного " +
                        "добавления")
        );
    }

    @Test
    void addEpicAndTwoTheSameHisSubtaskEpicAndOneSubtaskInHistory() {
        createTestSubtask();
        inMemoryHistoryManager.add(epic);
        inMemoryHistoryManager.add(subtask);
        inMemoryHistoryManager.add(subtask);
        var historyTasks = inMemoryHistoryManager.getHistory();
        assertAll(
                () -> assertTrue(historyTasks.size() == 2, "В historyList не хватает эпика или " +
                        "подзадачи"),
                () -> assertTrue(historyTasks.contains(epic), "В historyList не хватает эпика"),
                () -> assertTrue(historyTasks.contains(subtask), "В historyList не хватает подзадачи")
        );
    }

    @Test
    void addEpicSubtaskAndTaskAndTheSameSubtaskItShouldBeFirstInHistory() {
        createTestSubtask();
        createTestTask();
        inMemoryHistoryManager.add(epic);
        inMemoryHistoryManager.add(subtask);
        inMemoryHistoryManager.add(task);
        inMemoryHistoryManager.add(subtask);
        var historyTasks = inMemoryHistoryManager.getHistory();
        assertAll(
                () -> assertEquals(subtask, historyTasks.get(0), "Последняя просмотренная задача не в " +
                        "начале списка"),
                () -> assertTrue(historyTasks.size() == 3, "Список имеет неожиданный размер")
        );

    }

    //вспомогательные методы
    private void createTestTask() {
        task = new Task("Задача", "Описание задачи");
        inMemoryTaskManager.createTask(task);
    }

    private void createTestEpic() {
        epic = new Epic("Эпик", "Описание эпика");
        inMemoryTaskManager.createEpic(epic);
    }

    private void createTestSubtask() {
        epic = new Epic("Эпик", "Описание эпика");
        inMemoryTaskManager.createEpic(epic);
        subtask = new Subtask("Подзадача", "Описание подзадачи");
        inMemoryTaskManager.createSubtask(subtask, epic.getId());
    }
}