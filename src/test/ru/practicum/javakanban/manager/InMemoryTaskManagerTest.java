package ru.practicum.javakanban.manager;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ru.practicum.javakanban.model.Epic;
import ru.practicum.javakanban.model.Status;
import ru.practicum.javakanban.model.Subtask;
import ru.practicum.javakanban.model.Task;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    private InMemoryTaskManager inMemoryTaskManager;
    private HistoryManager historyManager;
    private Task task;
    private Epic epic;
    private Subtask subtask;

    @BeforeEach
    public void createTaskManager() {
        inMemoryTaskManager = new InMemoryTaskManager();
    }

    @Test
    public void createTaskTaskAddedToInMemoryManagerTasks() {
        task = new Task("Задача", "Описание задачи");
        inMemoryTaskManager.createTask(task);

        var tasks = inMemoryTaskManager.getTasks();

        assertAll(
                () -> assertNotNull(task.getId(), "Задаче не присвоился id"),
                () -> assertFalse(tasks.isEmpty(), "Задача не добавилась в tasks"),
                () -> assertEquals(task, tasks.get(task.getId()), "Задача не ищется в tasks по id")
        );
    }

    @Test
    public void createEpicAddedToInMemoryManagerEpics() {
        epic = new Epic("Эпик", "Описание эпика");
        inMemoryTaskManager.createEpic(epic);

        var epics = inMemoryTaskManager.getEpics();

        assertAll(
                () -> assertNotNull(epic.getId(), "Эпик не присвоился id"),
                () -> assertFalse(epics.isEmpty(), "Эпик не добавилась в epics"),
                () -> assertEquals(epic, epics.get(epic.getId()), "Эпик не ищется в epics по id")
        );
    }

    @Test
    public void createSubtaskAddedToInMemoryManagerSubtask() {
        epic = new Epic("Эпик", "Описание эпика");
        inMemoryTaskManager.createEpic(epic);
        subtask = new Subtask("Подзадача", "Описание подзадачи");
        inMemoryTaskManager.createSubtask(subtask, epic.getId());

        var subtasks = inMemoryTaskManager.getSubtasks();

        assertAll(
                () -> assertNotNull(subtask.getId(), "Подзадаче не присвоился id"),
                () -> assertFalse(subtasks.isEmpty(), "Подзадача не добавилась в subtasks"),
                () -> assertEquals(subtask, subtasks.get(subtask.getId()),
                        "Подзадача не ищется в subtasks по Id")

        );
    }

    @Test
    public void createSubtaskAddEpicSubtasks() {
        epic = new Epic("Эпик", "Описание эпика");
        inMemoryTaskManager.createEpic(epic);
        subtask = new Subtask("Подзадача", "Описание подзадачи");
        inMemoryTaskManager.createSubtask(subtask, epic.getId());

        var epicSubtasks = epic.getSubtasks();

        assertAll(
                () -> assertNotNull(subtask.getEpicId(), "Подзадаче не присвоился epicId"),
                () -> assertEquals(subtask.getEpicId(), epic.getId(),
                        "id эпика и epicId у подзадачи не одинаковы"),
                () -> assertTrue(epicSubtasks.contains(subtask), "В epicSubtask нет его подзадачи")
        );
    }

    @Test
    public void updateTaskTaskChangesInTasks() {
        task = new Task("Задача", "Описание задачи");
        inMemoryTaskManager.createTask(task);

        task.setStatus(Status.IN_PROGRESS);
        task.setName("Новое название задачи");
        task.setDescription("Новое описание задачи");
        var tasks = inMemoryTaskManager.getTasks();

        assertAll(
                () -> assertEquals(task.getName(), tasks.get(task.getId()).getName(),
                        "Имя задачи не изменилось"),
                () -> assertEquals(task.getDescription(), tasks.get(task.getId()).getDescription(),
                        "Описание задачи не изменилось"),
                () -> assertEquals(task.getStatus(), tasks.get(task.getId()).getStatus(),
                        "Статус задачи не изменился")
        );
    }

    @Test
    public void updateEpicEpicChangesInEpics() {
        epic = new Epic("Эпик", "Описание эпика");
        inMemoryTaskManager.createEpic(epic);

        epic.setName("Новое название эпика");
        epic.setDescription("Новое описание эпика");
        var epics = inMemoryTaskManager.getEpics();

        assertAll(
                () -> assertEquals(epic.getName(), epics.get(epic.getId()).getName(),
                        "Название эпика не изменилось"),
                () -> assertEquals(epic.getDescription(), epics.get(epic.getId()).getDescription(),
                        "Описание эпика не изменилось")
        );
    }

    @Test
    public void updateSubtaskSubtaskChangesInSabtasks() {
        epic = new Epic("Эпик", "Описание эпика");
        inMemoryTaskManager.createEpic(epic);
        subtask = new Subtask("Подзадача", "Описание подзадачи");
        inMemoryTaskManager.createSubtask(subtask, epic.getId());

        subtask.setName("Новое имя подзадачи");
        subtask.setDescription("Новое описание подзадачи");
        subtask.setStatus(Status.IN_PROGRESS);
        inMemoryTaskManager.updateSubtask(subtask);
        var subtasks = inMemoryTaskManager.getSubtasks();

        assertAll(
                () -> assertEquals(subtask.getName(), subtasks.get(subtask.getId()).getName(),
                        "У подзадачи не изменилось имя"),
                () -> assertEquals(subtask.getDescription(), subtasks.get(subtask.getId()).getDescription(),
                        "У подзадачи не изменилось описание"),
                () -> assertEquals(subtask.getStatus(), subtasks.get(subtask.getId()).getStatus(),
                        "У подзадачи не изменился статус")
        );
    }

    @Test
    public void updateSubtaskStatusChangeEpicStatus() {
        epic = new Epic("Эпик", "Описание эпика");
        inMemoryTaskManager.createEpic(epic);
        subtask = new Subtask("Подзадача", "Описание подзадачи");
        inMemoryTaskManager.createSubtask(subtask, epic.getId());

        subtask.setStatus(Status.IN_PROGRESS);
        inMemoryTaskManager.updateSubtask(subtask);
        var epics = inMemoryTaskManager.getEpics();

        assertTrue(epics.get(epic.getId()).getStatus() == Status.IN_PROGRESS,
                "У эпика не изменился статус");
    }

    @Test
    public void getEpicSubtasksCorrectIdReturnEpicSubtask() {
        epic = new Epic("Эпик", "Описание эпика");
        inMemoryTaskManager.createEpic(epic);
        subtask = new Subtask("Подзадача", "Описание подзадачи");
        inMemoryTaskManager.createSubtask(subtask, epic.getId());

        var epicSubtask = epic.getSubtasks();
        assertEquals(epicSubtask, inMemoryTaskManager.getEpicSubtasks(epic.getId()),
                "Не возвращаются подзадачи эпика");
    }

    @Test
    public void getEpicSubtasksIncorrectIdReturnNull() {
        assertNull(inMemoryTaskManager.getEpicSubtasks(0),
                "Эпика нет, но null не возвращается");
    }

    @Test
    public void deleteAllTasksTaskIsEmpty() {
        task = new Task("Задача", "Описание задачи");
        inMemoryTaskManager.createTask(task);

        inMemoryTaskManager.deleteAllTasks();
        assertTrue(inMemoryTaskManager.getTasks().isEmpty(), "Задачи не очистились");
    }

    @Test
    public void deleteAllEpicsSubtasksAndEpicsIsEmpty() {
        epic = new Epic("Эпик", "Описание эпика");
        inMemoryTaskManager.createEpic(epic);

        inMemoryTaskManager.deleteAllEpics();
        assertAll(
                () -> assertTrue(inMemoryTaskManager.getSubtasks().isEmpty(), "Подзадачи не очистились"),
                () -> assertTrue(inMemoryTaskManager.getEpics().isEmpty(), "Эпики не очистились")
        );
    }

    @Test
    public void deleteAllSubtasksSubtasksIsEmptyAndEpicHasNoSubtasks() {
        epic = new Epic("Эпик", "Описание эпика");
        inMemoryTaskManager.createEpic(epic);
        subtask = new Subtask("Подзадача", "Описание подзадачи");
        inMemoryTaskManager.createSubtask(subtask, epic.getId());

        inMemoryTaskManager.deleteAllSubtasks();
        assertAll(
                () -> assertTrue(inMemoryTaskManager.getSubtasks().isEmpty(), "Подзадачи не очистились"),
                () -> assertTrue(epic.getSubtasks().isEmpty(), "У эпика не очистились задачи")
        );
    }

    @Test
    public void getTaskCorrectIdReturnTask() {
        task = new Task("Задача", "Описание задачи");
        inMemoryTaskManager.createTask(task);

        assertEquals(task, inMemoryTaskManager.getTask(task.getId()), "Задача не вернулась по id");
    }

    @Test
    public void getTaskIncorrectIdReturnNull() {
        assertNull(inMemoryTaskManager.getTask(0), "Задачи нет, но null не возвращается");
    }

    @Test
    public void getEpicCorrectIdReturnEpic() {
        epic = new Epic("Эпик", "Описание эпика");
        inMemoryTaskManager.createEpic(epic);

        assertEquals(epic, inMemoryTaskManager.getEpic(epic.getId()), "Эпик не вернулся по id");
    }

    @Test
    public void getEpicIncorrectIdReturnNull() {
        assertNull(inMemoryTaskManager.getEpic(0), "Эпика нет, но null не возвращается");
    }

    @Test
    public void getSubtaskCorrectIdReturnSubtask() {
        epic = new Epic("Эпик", "Описание эпика");
        inMemoryTaskManager.createEpic(epic);
        subtask = new Subtask("Подзадача", "Описание подзадачи");
        inMemoryTaskManager.createSubtask(subtask, epic.getId());

        assertEquals(subtask, inMemoryTaskManager.getSubtask(subtask.getId()),
                "Подзадача не возвращается по id");
    }

    @Test
    public void getSubtaskIncorrectIdReturnNull() {
        assertNull(inMemoryTaskManager.getSubtask(0), "Подзадачи нет, но null не возвращается");
    }

    @Test
    public void deleteTaskCorrectIdTaskRemoveFromTasks() {
        task = new Task("Задача", "Описание задачи");
        inMemoryTaskManager.createTask(task);

        inMemoryTaskManager.deleteTask(task.getId());
        assertFalse(inMemoryTaskManager.getTasks().containsKey(task.getId()),
                "Задача не удалилась из tasks");
    }

    @Test
    public void deleteTaskIncorrectIdTasksSizeIsTheSame() {
        task = new Task("Задача", "Описание задачи");
        inMemoryTaskManager.createTask(task);
        int tasksSize = inMemoryTaskManager.getTasks().size();

        inMemoryTaskManager.deleteTask(123);
        assertTrue(tasksSize == inMemoryTaskManager.getTasks().size(),
                "Что-то удалилось из tasks, хотя удалять нечего");
    }

    @Test
    public void deleteEpicCorrectIdEpicRemoveFromEpics() {
        epic = new Epic("Эпик", "Описание эпика");
        inMemoryTaskManager.createEpic(epic);

        inMemoryTaskManager.deleteEpic(epic.getId());

        assertFalse(inMemoryTaskManager.getTasks().containsKey(epic.getId()),
                "Эпик не удалился из epics");
    }

    @Test
    public void deleteEpicCorrectIdEpicSubtasksRemoveFromSubtask() {
        epic = new Epic("Эпик", "Описание эпика");
        inMemoryTaskManager.createEpic(epic);
        subtask = new Subtask("Подзадача", "Описание подзадачи");
        inMemoryTaskManager.createSubtask(subtask, epic.getId());
        var epicSubtasks = epic.getSubtasks();

        inMemoryTaskManager.deleteEpic(epic.getId());
        for (Subtask epicSubtask : epicSubtasks) {
            assertFalse(inMemoryTaskManager.getSubtasks().containsKey(epicSubtask.getId()),
                    "Позадача не удалилась вместе с эпиком");
        }
    }

    @Test
    public void deleteEpicIncorrectIdEpicsSizeIsTheSame() {
        epic = new Epic("Эпик", "Описание эпика");
        inMemoryTaskManager.createEpic(epic);
        int epicsSize = inMemoryTaskManager.getEpics().size();

        inMemoryTaskManager.deleteEpic(123);
        assertTrue(epicsSize == inMemoryTaskManager.getEpics().size(),
                "Что-то удалилось из epics, хотя удалять нечего");
    }

    @Test
    public void deleteSubtaskCorrectIdSubtaskRemoveFromSubtask() {
        epic = new Epic("Эпик", "Описание эпика");
        inMemoryTaskManager.createEpic(epic);
        subtask = new Subtask("Подзадача", "Описание подзадачи");
        inMemoryTaskManager.createSubtask(subtask, epic.getId());

        inMemoryTaskManager.deleteSubtask(subtask.getId());

        assertAll(
                () -> assertFalse(epic.getSubtasks().contains(subtask),
                        "Подзадача не удалилась из epicSubtasks"),
                () -> assertFalse(inMemoryTaskManager.getSubtasks().containsKey(subtask.getId()),
                        "Подзадача не удалилась из subtasks")
        );
    }

    @Test
    public void deleteSubtaskIncorrectIdSubtasksIsTheSame() {
        epic = new Epic("Эпик", "Описание эпика");
        inMemoryTaskManager.createEpic(epic);
        subtask = new Subtask("Подзадача", "Описание подзадачи");
        inMemoryTaskManager.createSubtask(subtask, epic.getId());

        int subtasksSize = inMemoryTaskManager.getSubtasks().size();
        inMemoryTaskManager.deleteSubtask(123);

        assertTrue(subtasksSize == inMemoryTaskManager.getSubtasks().size(),
                "Что-то удалилось из subtasks, хотя удалять нечего");
    }

    //вспомогательные методы


}