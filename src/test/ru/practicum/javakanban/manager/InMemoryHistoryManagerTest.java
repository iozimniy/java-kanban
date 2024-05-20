package ru.practicum.javakanban.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.javakanban.model.Epic;
import ru.practicum.javakanban.model.Subtask;
import ru.practicum.javakanban.model.Task;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private HistoryManager inMemoryHistoryManager;
    private Task task;
    private Epic epic;
    private Subtask subtask;
    private final int historySize = InMemoryHistoryManager.getHistorySize();

    @BeforeEach
    public void createInMemoryHistoryManager() {
        inMemoryHistoryManager = new InMemoryHistoryManager();
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
    void add11thTaskItShouldBe10th() {
        for (int i = 0; i <= (historySize - 1); i++) {
            inMemoryHistoryManager.add(task);
        }

        inMemoryHistoryManager.add(epic);
        var historyTasks = inMemoryHistoryManager.getHistory();
        assertAll(
                () -> assertEquals(historySize, historyTasks.size(), "historyList распух"),
                () -> assertEquals(epic, historyTasks.get(historySize - 1), "Последняя задача не добавилась в конец")
        );
    }
}