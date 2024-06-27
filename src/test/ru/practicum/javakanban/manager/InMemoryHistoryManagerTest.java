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

    @BeforeEach
    public void createNewTasks() {
        task = new Task("Задача № 1", "Описание задачи № 1");
        epic = new Epic("Эпик № 1", "Описание эпика № 1");
        subtask = new Subtask("Подзадача № 1", "Описание подзадачи № 1");
  }

    @Test
    void addTaskItShouldBeInHistoryList() {
        inMemoryHistoryManager.add(task);
        var historyTasks = inMemoryHistoryManager.getHistory();
        assertAll(
                () -> assertEquals(1, historyTasks.size(), "При добавлении задачи historyList пуст"),
                () -> assertEquals(task, historyTasks.getFirst(), "Задача не добавилась в historyList")
        );
    }

    @Test
    void addEpicItShouldBeInHistoryList() {
        inMemoryHistoryManager.add(epic);
        var historyTasks = inMemoryHistoryManager.getHistory();
        assertAll(
                () -> assertEquals(1, historyTasks.size(), "При добавлении эпика historyList пуст"),
                () -> assertEquals(epic, historyTasks.getFirst(), "Эпик не добавился в historyList")
        );
    }

    @Test
    void addSubtaskItShouldBeInHistoryList() {
        inMemoryHistoryManager.add(subtask);
        var historyTasks = inMemoryHistoryManager.getHistory();
        assertAll(
                () -> assertEquals(1, historyTasks.size(), "При добавлении подзадачи historyList пуст"),
                () -> assertEquals(subtask, historyTasks.getFirst(), "Подзадача не добавилась в historyList")
        );
    }

    @Test
    void removeTaskItShouldNotBeInHistoryList() {
        inMemoryTaskManager.createTask(task);
        inMemoryHistoryManager.add(task);
        inMemoryHistoryManager.remove(task.getId());
        var historyTasks = inMemoryHistoryManager.getHistory();
        assertTrue(historyTasks.isEmpty(), "Задача не удалилась из historyList");
    }

    @Test
    void removeEpicItShouldNotBeInHistoryList() {
        inMemoryTaskManager.createEpic(epic);
        inMemoryHistoryManager.add(epic);
        inMemoryHistoryManager.remove(epic.getId());
        var historyTasks = inMemoryHistoryManager.getHistory();
        assertTrue(historyTasks.isEmpty(), "Эпик не удалился из historyList");
    }

    @Test
    void removeSubtaskItShouldNotBeInHistoryList() {
        inMemoryTaskManager.createEpic(epic);
        inMemoryTaskManager.createSubtask(subtask, epic.getId());
        inMemoryHistoryManager.add(subtask);
        inMemoryHistoryManager.remove(subtask.getId());
        var historyTasks = inMemoryHistoryManager.getHistory();
        assertTrue(historyTasks.isEmpty(), "Подзадача не удалилась из historyList");
    }


}