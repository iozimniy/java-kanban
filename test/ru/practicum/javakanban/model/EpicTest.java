package ru.practicum.javakanban.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.javakanban.exeptions.ManagerPrioritizeException;
import ru.practicum.javakanban.manager.Managers;
import ru.practicum.javakanban.manager.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    static TaskManager taskManager;
    Epic epic;
    Subtask subtask1;
    Subtask subtask2;
    Subtask subtask3;
    private static Duration SUBTASKS_DURATION = Duration.ofMinutes(30);

    private static LocalDateTime EARLY_DATE_TIME = LocalDateTime.of(2024, 8, 23, 14, 00);
    private static LocalDateTime LATE_DATE_TIME = LocalDateTime.of(2024, 9,10, 10, 30);

    @BeforeEach
    public void createTestEpic() throws ManagerPrioritizeException {
        taskManager = Managers.getDefault();
        epic = new Epic("Эпик", "Описание эпика");
        subtask1 = new Subtask("Подзадача № 1", "Описание подзадачи № 1", SUBTASKS_DURATION, EARLY_DATE_TIME);
        subtask2 = new Subtask("Подзадача № 2", "Описание подзадачи № 2", SUBTASKS_DURATION, LATE_DATE_TIME);
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask1, epic.getId());
        taskManager.createSubtask(subtask2, epic.getId());
        taskManager.updateEpic(epic, epic.getId());
    }


    @Test
    public void updateStatusAllSubtaskIsDoneEpicIsDone() throws ManagerPrioritizeException {
        Subtask updateSubtask1 = new Subtask(subtask1.getName(), subtask1.getDescription(), Status.DONE,
                subtask1.getDuration(), subtask1.getStartTime());
        taskManager.updateSubtask(updateSubtask1, subtask1.getId());

        Subtask updateSubtask2 = new Subtask(subtask2.getName(), subtask2.getDescription(), Status.DONE,
                subtask2.getDuration(), subtask2.getStartTime());
        taskManager.updateSubtask(updateSubtask2, subtask2.getId());

        assertSame(epic.getStatus(), Status.DONE);
    }

    @Test
    public void updateStatusAllSubtaskInProgressEpicInProgress() throws ManagerPrioritizeException {
        Subtask updateSubtask1 = new Subtask(subtask1.getName(), subtask1.getDescription(), Status.IN_PROGRESS,
                subtask1.getDuration(), subtask1.getStartTime());
        taskManager.updateSubtask(updateSubtask1, subtask1.getId());

        Subtask updateSubtask2 = new Subtask(subtask2.getName(), subtask2.getDescription(), Status.IN_PROGRESS,
                subtask2.getDuration(), subtask2.getStartTime());
        taskManager.updateSubtask(updateSubtask2, subtask2.getId());

        assertSame(epic.getStatus(), Status.IN_PROGRESS);
    }

    @Test
    public void updateStatusSubtasksNewAndInProgressEpicInProgress() throws ManagerPrioritizeException {
        Subtask updateSubtask1 = new Subtask(subtask1.getName(), subtask1.getDescription(), Status.NEW,
                subtask1.getDuration(), subtask1.getStartTime());
        taskManager.updateSubtask(updateSubtask1, subtask1.getId());

        Subtask updateSubtask2 = new Subtask(subtask2.getName(), subtask2.getDescription(), Status.IN_PROGRESS,
                subtask2.getDuration(), subtask2.getStartTime());
        taskManager.updateSubtask(updateSubtask2, subtask2.getId());

        assertSame(epic.getStatus(), Status.IN_PROGRESS);
    }

    @Test
    public void updateStatusSubtasksNewAndDoneEpicInProgress() throws ManagerPrioritizeException {
        Subtask updateSubtask1 = new Subtask(subtask1.getName(), subtask1.getDescription(), Status.NEW,
                subtask1.getDuration(), subtask1.getStartTime());
        taskManager.updateSubtask(updateSubtask1, subtask1.getId());

        Subtask updateSubtask2 = new Subtask(subtask2.getName(), subtask2.getDescription(), Status.DONE,
                subtask2.getDuration(), subtask2.getStartTime());
        taskManager.updateSubtask(updateSubtask2, subtask2.getId());

        assertSame(epic.getStatus(), Status.IN_PROGRESS);
    }

    @Test
    public void updateStatusSubtasksDoneAndInProgressEpicInProgress() throws ManagerPrioritizeException {
        Subtask updateSubtask1 = new Subtask(subtask1.getName(), subtask1.getDescription(), Status.DONE,
                subtask1.getDuration(), subtask1.getStartTime());
        taskManager.updateSubtask(updateSubtask1, subtask1.getId());

        Subtask updateSubtask2 = new Subtask(subtask2.getName(), subtask2.getDescription(), Status.IN_PROGRESS,
                subtask2.getDuration(), subtask2.getStartTime());
        taskManager.updateSubtask(updateSubtask2, subtask2.getId());

        assertSame(epic.getStatus(), Status.IN_PROGRESS);
    }

    @Test
    public void updateStatusDeleteAllSubtaskEpicIsDone() {
        taskManager.deleteSubtask(subtask1.getId());
        taskManager.deleteSubtask(subtask2.getId());

        assertSame(epic.getStatus(), Status.DONE);
    }

    @Test
    public void getStartTimeEARLY() {
        LocalDateTime epicStartTime = epic.getStartTime();

        assertTrue(EARLY_DATE_TIME.equals(epicStartTime), "У эпика неверное startTime!");
    }

    @Test
    public void getEndTimeLatestTime() {
        LocalDateTime epicEndTime = epic.getEndTime();
        LocalDateTime expectedEndTime = LATE_DATE_TIME.plus(SUBTASKS_DURATION);

        assertTrue(expectedEndTime.equals(epicEndTime), "У эпика неверное endTime!");
    }

    @Test
    public void getDurationAllSubtasksDuration() {
        Duration epicDuration = epic.getDuration();
        Duration expectedDuration = subtask1.getDuration().plus(subtask2.getDuration());

        assertTrue(expectedDuration.equals(epicDuration));
    }

    @Test
    public void epicWithoutSubtaskHasNullEndTime() {
        taskManager.deleteAllSubtasks();

        assertNull(epic.getEndTime());
    }
}