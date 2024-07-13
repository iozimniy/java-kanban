package ru.practicum.javakanban.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.javakanban.model.Epic;
import ru.practicum.javakanban.model.Status;
import ru.practicum.javakanban.model.Subtask;
import ru.practicum.javakanban.model.Task;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    private final Integer incorrectId = 1001;
    private InMemoryTaskManager inMemoryTaskManager;
    private Task task;
    private Epic epic;
    private Subtask subtask;

    @BeforeEach
    public void createTaskManager() {
        inMemoryTaskManager = new InMemoryTaskManager(Managers.getDefaultHistory());
    }

    @Test
    public void createTaskTaskAddedToInMemoryManagerTasks() {
        createTestTask();

        var tasks = inMemoryTaskManager.getTasks();

        assertAll(
                () -> assertNotNull(task.getId(), "Задаче не присвоился id"),
                () -> assertFalse(tasks.isEmpty(), "Задача не добавилась в tasks"),
                () -> assertEquals(task, tasks.get(task.getId()), "Задача не ищется в tasks по id")
        );
    }


    @Test
    public void createEpicAddedToInMemoryManagerEpics() {
        createTestEpic();

        var epics = inMemoryTaskManager.getEpics();

        assertAll(
                () -> assertNotNull(epic.getId(), "Эпик не присвоился id"),
                () -> assertFalse(epics.isEmpty(), "Эпик не добавилась в epics"),
                () -> assertEquals(epic, epics.get(epic.getId()), "Эпик не ищется в epics по id")
        );
    }


    @Test
    public void createSubtaskAddedToInMemoryManagerSubtask() {
        createTestEpic();
        createTestSubtask();

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
        createTestEpic();
        createTestSubtask();

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
        createTestTask();

        task.setStatus(Status.IN_PROGRESS);
        task.setName("Новое название задачи");
        task.setDescription("Новое описание задачи");
        inMemoryTaskManager.updateTask(task);
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
        createTestEpic();

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
    public void updateSubtaskSubtaskChangesInSubtasks() {
        createTestEpic();
        createTestSubtask();

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
        createTestEpic();
        createTestSubtask();

        subtask.setStatus(Status.IN_PROGRESS);
        inMemoryTaskManager.updateSubtask(subtask);
        var epics = inMemoryTaskManager.getEpics();

        assertSame(epics.get(epic.getId()).getStatus(), Status.IN_PROGRESS, "У эпика не изменился статус");
    }

    @Test
    public void getAllTasksReturnArrayListTasks() {
        createTestTask();

        ArrayList<Task> allTasks = new ArrayList<>(inMemoryTaskManager.getTasks().values());

        assertEquals(allTasks, inMemoryTaskManager.getAllTasks(), "getAllTasks не возвращает список задач");
    }

    @Test
    public void getAllEpicsReturnArrayListEpics() {
        createTestEpic();

        ArrayList<Epic> allEpics = new ArrayList<>(inMemoryTaskManager.getEpics().values());

        assertEquals(allEpics, inMemoryTaskManager.getAllEpics(), "getAllEpics не возвращает список эпиков");
    }

    @Test
    public void getAllSubtasksReturnArrayListSubtasks() {
        createTestEpic();
        createTestSubtask();

        ArrayList<Subtask> allSubtasks = new ArrayList<>(inMemoryTaskManager.getSubtasks().values());

        assertEquals(allSubtasks, inMemoryTaskManager.getAllSubtasks(),
                "getAllSubtasks не возвращает список подзадач");
    }

    @Test
    public void getEpicSubtasksCorrectIdReturnEpicSubtask() {
        createTestEpic();
        createTestSubtask();

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
        createTestTask();
        createTestTask();

        inMemoryTaskManager.deleteAllTasks();
        assertTrue(inMemoryTaskManager.getTasks().isEmpty(), "Задачи не очистились");
    }

    @Test
    public void deleteAllEpicsSubtasksAndEpicsIsEmpty() {
        createTestEpic();
        createTestEpic();

        inMemoryTaskManager.deleteAllEpics();
        assertAll(
                () -> assertTrue(inMemoryTaskManager.getSubtasks().isEmpty(), "Подзадачи не очистились"),
                () -> assertTrue(inMemoryTaskManager.getEpics().isEmpty(), "Эпики не очистились")
        );
    }

    @Test
    public void deleteAllSubtasksSubtasksIsEmptyAndEpicHasNoSubtasks() {
        createTestEpic();
        createTestSubtask();
        createTestSubtask();

        inMemoryTaskManager.deleteAllSubtasks();
        assertAll(
                () -> assertTrue(inMemoryTaskManager.getSubtasks().isEmpty(), "Подзадачи не очистились"),
                () -> assertTrue(epic.getSubtasks().isEmpty(), "У эпика не очистились задачи")
        );
    }

    @Test
    public void getTaskCorrectIdReturnTask() {
        createTestTask();

        assertEquals(task, inMemoryTaskManager.getTask(task.getId()), "Задача не вернулась по id");
    }

    @Test
    public void getTaskIncorrectIdReturnNull() {
        assertNull(inMemoryTaskManager.getTask(0), "Задачи нет, но null не возвращается");
    }

    @Test
    public void getTaskAddTaskInHistory() {
        createTestTask();

        inMemoryTaskManager.getTask(task.getId());

        assertTrue(inMemoryTaskManager.getHistory().contains(task));
    }

    @Test
    public void getEpicCorrectIdReturnEpic() {
        createTestEpic();

        assertEquals(epic, inMemoryTaskManager.getEpic(epic.getId()), "Эпик не вернулся по id");
    }

    @Test
    public void getEpicAddEpicInHistory() {
        createTestEpic();

        inMemoryTaskManager.getEpic(epic.getId());
        assertTrue(inMemoryTaskManager.getHistory().contains(epic));
    }

    @Test
    public void getEpicIncorrectIdReturnNull() {
        assertNull(inMemoryTaskManager.getEpic(0), "Эпика нет, но null не возвращается");
    }

    @Test
    public void getSubtaskCorrectIdReturnSubtask() {
        createTestEpic();
        createTestSubtask();

        assertEquals(subtask, inMemoryTaskManager.getSubtask(subtask.getId()),
                "Подзадача не возвращается по id");
    }

    @Test
    public void getSubtaskAddInHistory() {
        createTestEpic();
        createTestSubtask();
        inMemoryTaskManager.getSubtask(subtask.getId());

        assertTrue(inMemoryTaskManager.getHistory().contains(subtask));
    }

    @Test
    public void getSubtaskIncorrectIdReturnNull() {
        assertNull(inMemoryTaskManager.getSubtask(0), "Подзадачи нет, но null не возвращается");
    }

    @Test
    public void deleteTaskCorrectIdTaskRemoveFromTasks() {
        createTestTask();

        inMemoryTaskManager.deleteTask(task.getId());
        assertFalse(inMemoryTaskManager.getTasks().containsKey(task.getId()),
                "Задача не удалилась из tasks");
    }

    @Test
    public void deleteTaskIncorrectIdTasksSizeIsTheSame() {
        createTestTask();
        int tasksSize = inMemoryTaskManager.getTasks().size();

        inMemoryTaskManager.deleteTask(incorrectId);
        assertEquals(tasksSize, inMemoryTaskManager.getTasks().size(), "Что-то удалилось из tasks, хотя удалять нечего");
    }

    @Test
    public void deleteEpicCorrectIdEpicRemoveFromEpics() {
        createTestEpic();

        inMemoryTaskManager.deleteEpic(epic.getId());

        assertFalse(inMemoryTaskManager.getTasks().containsKey(epic.getId()),
                "Эпик не удалился из epics");
    }

    @Test
    public void deleteEpicCorrectIdEpicSubtasksRemoveFromSubtask() {
        createTestEpic();
        createTestSubtask();
        var epicSubtasks = epic.getSubtasks();

        inMemoryTaskManager.deleteEpic(epic.getId());
        for (Subtask epicSubtask : epicSubtasks) {
            assertFalse(inMemoryTaskManager.getSubtasks().containsKey(epicSubtask.getId()),
                    "Позадача не удалилась вместе с эпиком");
        }
    }

    @Test
    public void deleteEpicIncorrectIdEpicsSizeIsTheSame() {
        createTestEpic();
        int epicsSize = inMemoryTaskManager.getEpics().size();

        inMemoryTaskManager.deleteEpic(incorrectId);
        assertEquals(epicsSize, inMemoryTaskManager.getEpics().size(), "Что-то удалилось из epics, хотя удалять нечего");
    }

    @Test
    public void deleteSubtaskCorrectIdSubtaskRemoveFromSubtask() {
        createTestEpic();
        createTestSubtask();

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
        createTestEpic();
        createTestSubtask();

        int subtasksSize = inMemoryTaskManager.getSubtasks().size();
        inMemoryTaskManager.deleteSubtask(incorrectId);

        assertEquals(subtasksSize, inMemoryTaskManager.getSubtasks().size(), "Что-то удалилось из subtasks, хотя удалять нечего");
    }

    @Test
    public void deleteTaskItShouldNotBeInHistory() {
        createTestTask();
        inMemoryTaskManager.getTask(task.getId());
        inMemoryTaskManager.deleteTask(task.getId());
        assertTrue(inMemoryTaskManager.getHistory().isEmpty(), "Удалённая задача не удалилась из истории");
    }

    @Test
    public void deleteEpicItShouldNotBeInHistory() {
        createTestEpic();
        inMemoryTaskManager.getEpic(epic.getId());
        inMemoryTaskManager.deleteEpic(epic.getId());

        assertTrue(inMemoryTaskManager.getHistory().isEmpty(), "Удалённый эпик не удалилась из истории");
    }

    @Test
    public void deleteEpicWithSubtaskSubtaskDeleteFromHistory() {
        createTestEpic();
        createTestSubtask();
        inMemoryTaskManager.getEpic(epic.getId());
        inMemoryTaskManager.getSubtask(subtask.getId());
        inMemoryTaskManager.deleteEpic(epic.getId());

        assertTrue(inMemoryTaskManager.getHistory().isEmpty(), "История подозрительно полна");
    }

    @Test
    public void deleteSubtaskItShouldNotBeInHistory() {
        createTestEpic();
        createTestSubtask();
        inMemoryTaskManager.getSubtask(subtask.getId());
        inMemoryTaskManager.deleteSubtask(subtask.getId());

        assertTrue(inMemoryTaskManager.getHistory().isEmpty(), "Подзадача не удалилась из истории");
    }

    @Test
    public void deleteSubtaskEpicIsInHistory() {
        createTestEpic();
        createTestSubtask();
        inMemoryTaskManager.getEpic(epic.getId());
        inMemoryTaskManager.getSubtask(subtask.getId());
        inMemoryTaskManager.deleteSubtask(subtask.getId());

        assertAll(
                () -> assertTrue(inMemoryTaskManager.getHistory().contains(epic), "Эпик удалился из истории"),
                () -> assertEquals(1, inMemoryTaskManager.getHistory().size(), "Неожиданный размер " +
                        "истории")
        );
    }

    @Test
    public void deleteAllTasksHistoryIsEmpty() {
        createTestTask();
        inMemoryTaskManager.getTask(task.getId());
        inMemoryTaskManager.deleteAllTasks();

        assertTrue(inMemoryTaskManager.getHistory().isEmpty(), "История подозрительно полна");
    }

    @Test
    public void deleteAllEpicsHistoryIsEmpty() {
        createTestEpic();
        inMemoryTaskManager.getEpic(epic.getId());
        inMemoryTaskManager.deleteAllEpics();

        assertTrue(inMemoryTaskManager.getHistory().isEmpty(), "История подозрительно полна");
    }

    @Test
    public void deleteAllEpicsWithSubtaskHistoryIsEmpty() {
        createTestEpic();
        createTestSubtask();
        inMemoryTaskManager.getEpic(epic.getId());
        inMemoryTaskManager.getSubtask(subtask.getId());
        inMemoryTaskManager.deleteAllEpics();

        assertTrue(inMemoryTaskManager.getHistory().isEmpty(), "История подозрительно полна");
    }

    @Test
    public void deleteAllSubtasksHistoryIsEmpty() {
        createTestEpic();
        createTestSubtask();
        inMemoryTaskManager.getSubtask(subtask.getId());
        inMemoryTaskManager.deleteAllSubtasks();

        assertTrue(inMemoryTaskManager.getHistory().isEmpty(), "История подозрительно полна");
    }

    @Test
    public void deleteAllSubtasksEpicIsInHistory() {
        createTestEpic();
        createTestSubtask();
        inMemoryTaskManager.getEpic(epic.getId());
        inMemoryTaskManager.getSubtask(subtask.getId());
        inMemoryTaskManager.deleteAllSubtasks();

        assertAll(
                () -> assertTrue(inMemoryTaskManager.getHistory().contains(epic), "Эпик удалился из истории"),
                () -> assertEquals(1, inMemoryTaskManager.getHistory().size(), "Неожиданный размер " +
                        "истории")
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
        subtask = new Subtask("Подзадача", "Описание подзадачи");
        inMemoryTaskManager.createSubtask(subtask, epic.getId());
    }


}