package ru.practicum.javakanban.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.javakanban.exeptions.ManagerPrioritizeException;
import ru.practicum.javakanban.model.Epic;
import ru.practicum.javakanban.model.Status;
import ru.practicum.javakanban.model.Subtask;
import ru.practicum.javakanban.model.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public abstract class ManagersTest {
    protected final Integer incorrectId = 1001;
    protected TaskManager taskManager;
    protected Task task;
    protected Task task1;
    protected Task task2;
    protected Epic epic;
    protected Subtask subtask;
    protected static LocalDateTime TASKS_DATE_TIME = LocalDateTime.of(2024,12,31,12,30);
    protected static Duration TASKS_DURATION = Duration.ofMinutes(30);

    @BeforeEach
    public abstract void createTaskManager() throws IOException;

    @Test
    public void createTaskTaskAddedToInMemoryManagerTasks() {
        createTestTask();

        var tasks = taskManager.getTasks();

        assertAll(
                () -> assertNotNull(task.getId(), "Задаче не присвоился id"),
                () -> assertFalse(tasks.isEmpty(), "Задача не добавилась в tasks"),
                () -> assertEquals(task, tasks.get(task.getId()), "Задача не ищется в tasks по id")
        );
    }


    @Test
    public void createEpicAddedToInMemoryManagerEpics() {
        createTestEpic();

        var epics = taskManager.getEpics();

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

        var subtasks = taskManager.getSubtasks();

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
                () -> assertEquals(epic.getId(), subtask.getEpicId(),
                        "id эпика и epicId у подзадачи не одинаковы"),
                () -> assertTrue(epicSubtasks.contains(subtask), "В epicSubtask нет его подзадачи")
        );
    }

    @Test
    public void updateTaskTaskChangesInTasks() {
        createTestTask();
        Task updateTask = new Task("Новое название задачи", "Новое описание задачи", task.getStatus(),
                task.getDuration(), task.getStartTime());

        taskManager.updateTask(updateTask, task.getId());
        var tasks = taskManager.getTasks();

        assertAll(
                () -> assertEquals(updateTask.getName(), tasks.get(task.getId()).getName(),
                        "Имя задачи не изменилось"),
                () -> assertEquals(updateTask.getDescription(), tasks.get(task.getId()).getDescription(),
                        "Описание задачи не изменилось"),
                () -> assertEquals(updateTask.getStatus(), tasks.get(task.getId()).getStatus(),
                        "Статус задачи не изменился"),
                () -> assertSame(1, tasks.size(), "Подозрительное количество задач в tasks после обновления " +
                        "задачи")
        );
    }

    @Test
    public void updateEpicEpicChangesInEpics() {
        createTestEpic();

        Epic updateEpic = new Epic("Новое название эпика", "Новое описание эпика");
        taskManager.updateEpic(updateEpic, epic.getId());

        var epics = taskManager.getEpics();

        assertAll(
                () -> assertEquals(updateEpic.getName(), epics.get(epic.getId()).getName(),
                        "Название эпика не изменилось"),
                () -> assertEquals(updateEpic.getDescription(), epics.get(epic.getId()).getDescription(),
                        "Описание эпика не изменилось"),
                () -> assertSame(1, epics.size(), "Подозрительное количество эпиков в epics после обновления" +
                        " эпика")
        );
    }

    @Test
    public void updateSubtaskSubtaskChangesInSubtasks() {
        createTestEpic();
        createTestSubtask();

        Subtask updateSubtask = new Subtask("Новое имя подзадачи", "Новое описание подзадачи",
                Status.IN_PROGRESS, subtask.getDuration(), subtask.getStartTime());

        taskManager.updateSubtask(updateSubtask, subtask.getId());
        var subtasks = taskManager.getSubtasks();

        assertAll(
                () -> assertEquals(updateSubtask.getName(), subtasks.get(subtask.getId()).getName(),
                        "У подзадачи не изменилось имя"),
                () -> assertEquals(updateSubtask.getDescription(), subtasks.get(subtask.getId()).getDescription(),
                        "У подзадачи не изменилось описание"),
                () -> assertEquals(updateSubtask.getStatus(), subtasks.get(subtask.getId()).getStatus(),
                        "У подзадачи не изменился статус"),
                () -> assertSame(1, subtasks.size(), "Подозрительное количество подзадач в subtasks " +
                        "после обновления подзадачи"),
                () -> assertSame(1, epic.getSubtasks().size(), "Подозрительное количесто задач у эпика" +
                        "после обновления подзадачи")
        );
    }

    @Test
    public void updateSubtaskStatusChangeEpicStatus() {
        createTestEpic();
        createTestSubtask();

        Subtask updateSubtask = new Subtask(subtask.getName(), subtask.getDescription(), Status.IN_PROGRESS,
                subtask.getDuration(), subtask.getStartTime());

        taskManager.updateSubtask(updateSubtask, subtask.getId());
        var epics = taskManager.getEpics();

        assertSame(epics.get(epic.getId()).getStatus(), Status.IN_PROGRESS, "У эпика не изменился статус");
    }

    @Test
    public void updateSubtaskDurationChangesEpicDurationAndEndTime() {
        createTestEpic();
        createTestSubtask();

        Subtask updateSubtask = new Subtask(subtask.getName(), subtask.getDescription(), subtask.getStatus(),
                Duration.ofMinutes(40), subtask.getStartTime());

        taskManager.updateSubtask(updateSubtask, subtask.getId());

        assertAll(
                () -> assertTrue(updateSubtask.getDuration().equals(epic.getDuration()), "У эпика его сабтаски" +
                        "разные Duration."),
                () -> assertTrue(updateSubtask.getEndTime().equals(epic.getEndTime()), "У эпика и его сабтаски разные " +
                        "endTime")
        );
    }

    @Test
    public void updateSubtaskStartTimeChangesEpicStartTime() {
        createTestEpic();
        createTestSubtask();

        Subtask updateSubtask = new Subtask(subtask.getName(), subtask.getDescription(), subtask.getStatus(),
                subtask.getDuration(), LocalDateTime.of(2024, 10, 17, 15, 20));
        taskManager.updateSubtask(updateSubtask, subtask.getId());

        assertTrue(updateSubtask.getStartTime().equals(epic.getStartTime()), "У эпика и его подзадачи разные" +
                "startTime");
    }

    @Test
    public void getAllTasksReturnArrayListTasks() {
        createTestTask();

        ArrayList<Task> allTasks = new ArrayList<>(taskManager.getTasks().values());

        assertEquals(allTasks, taskManager.getAllTasks(), "getAllTasks не возвращает список задач");
    }

    @Test
    public void getAllEpicsReturnArrayListEpics() {
        createTestEpic();

        ArrayList<Epic> allEpics = new ArrayList<>(taskManager.getEpics().values());

        assertEquals(allEpics, taskManager.getAllEpics(), "getAllEpics не возвращает список эпиков");
    }

    @Test
    public void getAllSubtasksReturnArrayListSubtasks() {
        createTestEpic();
        createTestSubtask();

        ArrayList<Subtask> allSubtasks = new ArrayList<>(taskManager.getSubtasks().values());

        assertEquals(allSubtasks, taskManager.getAllSubtasks(),
                "getAllSubtasks не возвращает список подзадач");
    }

    @Test
    public void getEpicSubtasksCorrectIdReturnEpicSubtask() {
        createTestEpic();
        createTestSubtask();

        var epicSubtask = epic.getSubtasks();
        assertEquals(epicSubtask, taskManager.getEpicSubtasks(epic.getId()),
                "Не возвращаются подзадачи эпика");
    }

    @Test
    public void getEpicSubtasksIncorrectIdReturnNull() {
        assertNull(taskManager.getEpicSubtasks(0),
                "Эпика нет, но null не возвращается");
    }

    @Test
    public void deleteAllTasksTaskIsEmpty() {
        createTestTask();
        task2 = new Task("Другая задача", "Другое описание", Duration.ofMinutes(30),
                LocalDateTime.of(2024, 11, 17, 11, 20));
        taskManager.createTask(task2);

        taskManager.deleteAllTasks();
        assertTrue(taskManager.getTasks().isEmpty(), "Задачи не очистились");
    }

    @Test
    public void deleteAllEpicsSubtasksAndEpicsIsEmpty() {
        createTestEpic();
        createTestEpic();

        taskManager.deleteAllEpics();
        assertAll(
                () -> assertTrue(taskManager.getSubtasks().isEmpty(), "Подзадачи не очистились"),
                () -> assertTrue(taskManager.getEpics().isEmpty(), "Эпики не очистились")
        );
    }

    @Test
    public void deleteAllSubtasksSubtasksIsEmptyAndEpicHasNoSubtasks() {
        createTestEpic();
        createTestSubtask();

        Subtask otherSubtask = new Subtask("Ещё одна подзадача", "Описание", Duration.ofMinutes(60),
                LocalDateTime.of(2024, 11, 17, 11, 20));

        taskManager.deleteAllSubtasks();
        assertAll(
                () -> assertTrue(taskManager.getSubtasks().isEmpty(), "Подзадачи не очистились"),
                () -> assertTrue(epic.getSubtasks().isEmpty(), "У эпика не очистились задачи")
        );
    }

    @Test
    public void getTaskCorrectIdReturnTask() {
        createTestTask();

        assertEquals(task, taskManager.getTask(task.getId()), "Задача не вернулась по id");
    }

    @Test
    public void getTaskIncorrectIdReturnNull() {
        assertNull(taskManager.getTask(0), "Задачи нет, но null не возвращается");
    }

    @Test
    public void getTaskAddTaskInHistory() {
        createTestTask();

        taskManager.getTask(task.getId());

        assertTrue(taskManager.getHistory().contains(task), "Задача не сохранилась в истории после get");
    }

    @Test
    public void getEpicCorrectIdReturnEpic() {
        createTestEpic();

        assertEquals(epic, taskManager.getEpic(epic.getId()), "Эпик не вернулся по id");
    }

    @Test
    public void getEpicAddEpicInHistory() {
        createTestEpic();

        taskManager.getEpic(epic.getId());
        assertTrue(taskManager.getHistory().contains(epic), "Эпик не сохранился в истории после get");
    }

    @Test
    public void getEpicIncorrectIdReturnNull() {
        assertNull(taskManager.getEpic(0), "Эпика нет, но null не возвращается");
    }

    @Test
    public void getSubtaskCorrectIdReturnSubtask() {
        createTestEpic();
        createTestSubtask();

        assertEquals(subtask, taskManager.getSubtask(subtask.getId()),
                "Подзадача не возвращается по id");
    }

    @Test
    public void getSubtaskAddInHistory() {
        createTestEpic();
        createTestSubtask();
        taskManager.getSubtask(subtask.getId());

        assertTrue(taskManager.getHistory().contains(subtask), "Подзадачи нет в истории после get");
    }

    @Test
    public void getSubtaskIncorrectIdReturnNull() {
        assertNull(taskManager.getSubtask(0), "Подзадачи нет, но null не возвращается");
    }

    @Test
    public void deleteTaskCorrectIdTaskRemoveFromTasks() {
        createTestTask();

        taskManager.deleteTask(task.getId());
        assertFalse(taskManager.getTasks().containsKey(task.getId()),
                "Задача не удалилась из tasks");
    }

    @Test
    public void deleteTaskIncorrectIdTasksSizeIsTheSame() {
        createTestTask();
        int tasksSize = taskManager.getTasks().size();

        taskManager.deleteTask(incorrectId);
        assertEquals(tasksSize, taskManager.getTasks().size(), "Что-то удалилось из tasks, хотя удалять нечего");
    }

    @Test
    public void deleteEpicCorrectIdEpicRemoveFromEpics() {
        createTestEpic();

        taskManager.deleteEpic(epic.getId());

        assertFalse(taskManager.getTasks().containsKey(epic.getId()),
                "Эпик не удалился из epics");
    }

    @Test
    public void deleteEpicCorrectIdEpicSubtasksRemoveFromSubtask() {
        createTestEpic();
        createTestSubtask();
        var epicSubtasks = epic.getSubtasks();

        taskManager.deleteEpic(epic.getId());
        for (Subtask epicSubtask : epicSubtasks) {
            assertFalse(taskManager.getSubtasks().containsKey(epicSubtask.getId()),
                    "Позадача не удалилась вместе с эпиком");
        }
    }

    @Test
    public void deleteEpicIncorrectIdEpicsSizeIsTheSame() {
        createTestEpic();
        int epicsSize = taskManager.getEpics().size();

        taskManager.deleteEpic(incorrectId);
        assertEquals(epicsSize, taskManager.getEpics().size(), "Что-то удалилось из epics, хотя удалять нечего");
    }

    @Test
    public void deleteSubtaskCorrectIdSubtaskRemoveFromSubtask() {
        createTestEpic();
        createTestSubtask();

        taskManager.deleteSubtask(subtask.getId());

        assertAll(
                () -> assertFalse(epic.getSubtasks().contains(subtask),
                        "Подзадача не удалилась из epicSubtasks"),
                () -> assertFalse(taskManager.getSubtasks().containsKey(subtask.getId()),
                        "Подзадача не удалилась из subtasks")
        );
    }

    @Test
    public void deleteSubtaskIncorrectIdSubtasksIsTheSame() {
        createTestEpic();
        createTestSubtask();

        int subtasksSize = taskManager.getSubtasks().size();
        taskManager.deleteSubtask(incorrectId);

        assertEquals(subtasksSize, taskManager.getSubtasks().size(), "Что-то удалилось из subtasks, хотя удалять нечего");
    }

    @Test
    public void deleteTaskItShouldNotBeInHistory() {
        createTestTask();
        taskManager.getTask(task.getId());
        taskManager.deleteTask(task.getId());
        assertTrue(taskManager.getHistory().isEmpty(), "Удалённая задача не удалилась из истории");
    }

    @Test
    public void deleteEpicItShouldNotBeInHistory() {
        createTestEpic();
        taskManager.getEpic(epic.getId());
        taskManager.deleteEpic(epic.getId());

        assertTrue(taskManager.getHistory().isEmpty(), "Удалённый эпик не удалилась из истории");
    }

    @Test
    public void deleteEpicWithSubtaskSubtaskDeleteFromHistory() {
        createTestEpic();
        createTestSubtask();
        taskManager.getEpic(epic.getId());
        taskManager.getSubtask(subtask.getId());
        taskManager.deleteEpic(epic.getId());

        assertTrue(taskManager.getHistory().isEmpty(), "История подозрительно полна");
    }

    @Test
    public void deleteSubtaskItShouldNotBeInHistory() {
        createTestEpic();
        createTestSubtask();
        taskManager.getSubtask(subtask.getId());
        taskManager.deleteSubtask(subtask.getId());

        assertTrue(taskManager.getHistory().isEmpty(), "Подзадача не удалилась из истории");
    }

    @Test
    public void deleteSubtaskEpicIsInHistory() {
        createTestEpic();
        createTestSubtask();
        taskManager.getEpic(epic.getId());
        taskManager.getSubtask(subtask.getId());
        taskManager.deleteSubtask(subtask.getId());

        assertAll(
                () -> assertTrue(taskManager.getHistory().contains(epic), "Эпик удалился из истории"),
                () -> assertEquals(1, taskManager.getHistory().size(), "Неожиданный размер " +
                        "истории")
        );
    }

    @Test
    public void deleteAllTasksHistoryIsEmpty() {
        createTestTask();
        taskManager.getTask(task.getId());
        taskManager.deleteAllTasks();

        assertTrue(taskManager.getHistory().isEmpty(), "История подозрительно полна");
    }

    @Test
    public void deleteAllEpicsHistoryIsEmpty() {
        createTestEpic();
        taskManager.getEpic(epic.getId());
        taskManager.deleteAllEpics();

        assertTrue(taskManager.getHistory().isEmpty(), "История подозрительно полна");
    }

    @Test
    public void deleteAllEpicsWithSubtaskHistoryIsEmpty() {
        createTestEpic();
        createTestSubtask();
        taskManager.getEpic(epic.getId());
        taskManager.getSubtask(subtask.getId());
        taskManager.deleteAllEpics();

        assertTrue(taskManager.getHistory().isEmpty(), "История подозрительно полна");
    }

    @Test
    public void deleteAllSubtasksHistoryIsEmpty() {
        createTestEpic();
        createTestSubtask();
        taskManager.getSubtask(subtask.getId());
        taskManager.deleteAllSubtasks();

        assertTrue(taskManager.getHistory().isEmpty(), "История подозрительно полна");
    }

    @Test
    public void deleteAllSubtasksEpicIsInHistory() {
        createTestEpic();
        createTestSubtask();
        taskManager.getEpic(epic.getId());
        taskManager.getSubtask(subtask.getId());
        taskManager.deleteAllSubtasks();

        assertAll(
                () -> assertTrue(taskManager.getHistory().contains(epic), "Эпик удалился из истории"),
                () -> assertEquals(1, taskManager.getHistory().size(), "Неожиданный размер " +
                        "истории")
        );
    }

    @Test
    public void getPrioritizedTask() {
        createTaskForPrioritized();

        assertAll(
                () -> assertSame(task2, taskManager.getPrioritizedTasks().getFirst(),
                        "Задача № 2 не первая"),
                () -> assertSame(subtask, taskManager.getPrioritizedTasks().getLast(),
                        "Подзадача не последняя"),
                () -> assertSame(3, taskManager.getPrioritizedTasks().size(),
                        "В приоритизированном списке подозрительное количество задач")
        );
    }

    @Test
    public void deleteTaskItRemoveFromPrioritizedTask() {
        createTaskForPrioritized();

        taskManager.deleteTask(task2.getId());

        assertFalse(taskManager.getPrioritizedTasks().contains(task2), "Задача не удалилась из списка" +
                "приоритизации");
    }

    @Test
    public void deleteSubtaskItRemoveFromPrioritizedTask() {
        createTaskForPrioritized();

        taskManager.deleteSubtask(subtask.getId());

        assertFalse(taskManager.getPrioritizedTasks().contains(subtask), "Подзадача не удалилась из " +
                "списка приоритезации");
    }

    @Test
    public void updateTaskStartTimeChangePrioritizedTask() {
        createTaskForPrioritized();
        Task updateTask2 = new Task(task2.getName(), task2.getDescription(), task2.getStatus(), task2.getDuration(),
                LocalDateTime.of(2025, 1, 15, 15, 0));

        taskManager.updateTask(updateTask2, task2.getId());

        assertAll(
                () -> assertSame(updateTask2, taskManager.getPrioritizedTasks().getLast(), "Список " +
                        "приоритизации имеет странный порядок"),
                () -> assertSame(task1, taskManager.getPrioritizedTasks().getFirst(), "Список " +
                        "приоритизации имеет странный порядок")
        );

    }

    @Test
    public void updateSubtaskStartTimeChangePrioritizedTask() {
        createTaskForPrioritized();
        Subtask updateSubtask = new Subtask(subtask.getName(), subtask.getDescription(), subtask.getStatus(),
                subtask.getDuration(), LocalDateTime.of(2025, 1, 15, 15, 0));
        taskManager.updateSubtask(updateSubtask, subtask.getId());

        assertAll(
                () -> assertSame(task2, taskManager.getPrioritizedTasks().getFirst(), "Список " +
                        "приоритезации имеет странный порядок"),
                () -> assertSame(updateSubtask, taskManager.getPrioritizedTasks().getLast(), "Список" +
                        "приоритезации имеет странный порядок")
        );
    }

    @Test
    public void updateSubtaskStartTimeNullException() {
        createTestEpic();
        createTestSubtask();

        Subtask updateSubtask = new Subtask(subtask.getName(), subtask.getDescription(), subtask.getStatus(),
                subtask.getDuration(), null);

        assertThrows(IllegalArgumentException.class, () -> taskManager.updateSubtask(updateSubtask, subtask.getId()));
    }

    @Test
    public void createTaskNullStarTimeException() {
        task = new Task("Название", "Описание", Duration.ofMinutes(30), null);

        assertThrows(IllegalArgumentException.class, () -> taskManager.createTask(task));
    }

    @Test
    public void updateTaskNullException() {
        createTestTask();
        Task updateTask = new Task(task.getName(), task.getDescription(), task.getStatus(), task.getDuration(), null);

        assertThrows(IllegalArgumentException.class, () -> taskManager.updateTask(updateTask, task.getId()));
    }

    @Test
    public void createSubtaskNullStarTimeException() {
        createTestEpic();
        subtask = new Subtask("Название", "Описание", Duration.ofMinutes(30), null);

        assertThrows(IllegalArgumentException.class, () -> taskManager.createSubtask(subtask, epic.getId()));
    }

    @Test
    public void createTaskCrossedTimesException() {
        createTestTask();
        task1 = new Task("Другая задача", "Тестовое описание", Duration.ofMinutes(60),
                LocalDateTime.of(2024, 12, 31, 12, 15));
        task2 = new Task("Ещё одна задача", "Тестовое описание", Duration.ofMinutes(40),
                LocalDateTime.of(2024, 12, 31, 12, 0));
        Task task3 = new Task("И снова задача", "Тестовое описание", Duration.ofMinutes(10),
                LocalDateTime.of(2024, 12, 31, 12, 40));
        Task task4 = new Task("О! Задача!", "Тестовое описание", Duration.ofMinutes(240),
                LocalDateTime.of(2024, 12, 31, 10, 0));
        Task task5 = new Task("О! Задача!", "Тестовое описание", Duration.ofMinutes(10),
                LocalDateTime.of(2024, 12, 31, 12, 30));

        assertThrows(ManagerPrioritizeException.class, () ->
                taskManager.createTask(task1), "Нет исключения при проверке валидации! Пересечение" +
                "в конце периода первой задачи");
        assertThrows(ManagerPrioritizeException.class, () ->
                taskManager.createTask(task2), "Нет исключения при проверке валидации! Пересечение" +
                "в начале периода первой задачи");
        assertThrows(ManagerPrioritizeException.class, () ->
                taskManager.createTask(task3), "Нет исключения при проверке валидации! Период второй " +
                "задачи внутри первой");
        assertThrows(ManagerPrioritizeException.class, () ->
                taskManager.createTask(task4), "Нет исключения при проверке валидации! Период первой " +
                "задачи внутри внутри");
        assertThrows(ManagerPrioritizeException.class, () ->
                taskManager.createTask(task5), "Нет исключения при проверке валидации! Одинаковое " +
                "startTime");
    }

    @Test
    public void createTaskWithoutCrossedTimeWithoutException() {
        createTestTask();
        task1 = new Task("Другая задача", "Тестовое описание", Duration.ofMinutes(29),
                LocalDateTime.of(2024, 12, 31, 12, 0));
        task2 = new Task("Другая задача", "Тестовое описание", Duration.ofMinutes(30),
                LocalDateTime.of(2024, 12, 31, 13, 1));

        taskManager.createTask(task1);
        taskManager.createTask(task2);

        assertSame(3, taskManager.getTasks().size(), "Пробоема граничных значений при " +
                "добавлении задач");
    }

    @Test
    public void createSubtaskCrossedTimesException() {
        createTestTask();
        createTestEpic();

        subtask = new Subtask("Подзадача", "Тестовое описание", Duration.ofMinutes(60),
                LocalDateTime.of(2024, 12, 31, 12, 15));

        assertThrows(ManagerPrioritizeException.class, () ->
                taskManager.createSubtask(subtask, epic.getId()), "У сабтаски не проверяется время!");
    }

    @Test
    public void updateTaskCrossedWithTaskException() {
        createTaskForPrioritized();

        Task updateTask2 = new Task(task2.getName(), task2.getDescription(), task2.getStatus(), Duration.ofMinutes(60),
                LocalDateTime.of(2024, 10, 15, 12, 0));

        assertThrows(ManagerPrioritizeException.class, () -> taskManager.updateTask(updateTask2, task2.getId()),
                "Апдейт задачи с пересечением с другой задачей прошёл успешно");
    }

    @Test
    public void updateSubtaskCrossedTaskException() {
        createTaskForPrioritized();

        Subtask updateSubtask = new Subtask(subtask.getName(), subtask.getDescription(), subtask.getStatus(),
                Duration.ofMinutes(60), LocalDateTime.of(2024, 10, 15, 12, 0));

        assertThrows(ManagerPrioritizeException.class, () ->
                taskManager.updateSubtask(updateSubtask, subtask.getId()), "Апдейт подзадачи с пересечением" +
                "с задачей прошёл успешно");
    }

    @Test
    public void updateTaskCrossedSubtaskException() {
        createTestEpic();
        createTestSubtask();

        task = new Task("Задача", "Описание", Duration.ofMinutes(60),
                LocalDateTime.of(2024, 12, 31, 11, 0));

        taskManager.createTask(task);

        Task updateTask = new Task(task.getName(), task.getDescription(), task.getStatus(), Duration.ofMinutes(60),
                LocalDateTime.of(2024, 12, 31, 12, 0));

        assertThrows(ManagerPrioritizeException.class, () -> taskManager.updateTask(updateTask, task.getId()),
                "Апдейт задачи с пересечением по времени с подзадачей прощёд успешно");
    }

    @Test
    public void deleteEpicHisSubtaskDeleteFromPrioritizedTasks() {
        createTestEpic();
        createTestSubtask();
        Subtask subtask1 = new Subtask("Название", "Описание", Duration.ofMinutes(30),
                LocalDateTime.of(2024, 11, 17, 11, 20));
        taskManager.createSubtask(subtask1, epic.getId());

        taskManager.deleteEpic(epic.getId());

        assertTrue(taskManager.getPrioritizedTasks().isEmpty(), "После удаления эпика его подзадачи" +
                "не удалились из приоритезации");
    }


    //вспомогательные методы
    private void createTestTask() {
        task = new Task("Задача", "Описание задачи", TASKS_DURATION, TASKS_DATE_TIME);
        taskManager.createTask(task);
    }

    private void createTestEpic() {
        epic = new Epic("Эпик", "Описание эпика");
        taskManager.createEpic(epic);
    }

    private void createTestSubtask() {
        subtask = new Subtask("Подзадача", "Описание подзадачи", TASKS_DURATION, TASKS_DATE_TIME);
        taskManager.createSubtask(subtask, epic.getId());
    }

    private void createTaskForPrioritized() {
        task1 = new Task("Задача № 1", "Описание задачи № 1", TASKS_DURATION,
                LocalDateTime.of(2024, 10, 15, 12, 30));
        taskManager.createTask(task1);

        epic = new Epic("Эпик", "Описание эпика");
        taskManager.createEpic(epic);

        subtask = new Subtask("Подзадача", "Описание подзадачи", TASKS_DURATION,
                LocalDateTime.of(2024, 11, 5, 15, 0));
        taskManager.createSubtask(subtask, epic.getId());

        task2 = new Task("Задача № 2", "Описание задачи № 2", TASKS_DURATION,
                LocalDateTime.of(2024, 9, 7, 10, 15));
        taskManager.createTask(task2);
    }

}
