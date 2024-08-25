package ru.practicum.javakanban.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.javakanban.manager.Managers;
import ru.practicum.javakanban.manager.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    public void createTestEpic() {
        taskManager = Managers.getDefault();
        epic = new Epic("Эпик", "Описание эпика");
        subtask1 = new Subtask("Подзадача № 1", "Описание подзадачи № 1", SUBTASKS_DURATION,
                EARLY_DATE_TIME);
        subtask2 = new Subtask("Подзадача № 2", "Описание подзадачи № 2", SUBTASKS_DURATION,
                LATE_DATE_TIME);
        subtask3 = new Subtask("Подзадача № 3", "Описание подзадачи № 3");
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask1, epic.getId());
        taskManager.createSubtask(subtask2, epic.getId());
        taskManager.createSubtask(subtask3, epic.getId());
        taskManager.updateEpic(epic);
    }


    @Test
    public void updateStatusAllSubtaskIsDoneEpicIsDone() {
        subtask1.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);
        subtask2.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask2);
        subtask3.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask3);

        assertSame(epic.getStatus(), Status.DONE);
    }

    @Test
    public void updateStatusAllSubtaskInProgressEpicInProgress() {
        subtask1.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask1);
        subtask2.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask2);

        assertSame(epic.getStatus(), Status.IN_PROGRESS);
    }

    @Test
    public void updateStatusSubtasksNewAndInProgressEpicInProgress() {
        subtask1.setStatus(Status.NEW);
        taskManager.updateSubtask(subtask1);
        subtask2.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask2);

        assertSame(epic.getStatus(), Status.IN_PROGRESS);
    }

    @Test
    public void updateStatusSubtasksNewAndDoneEpicInProgress() {
        subtask1.setStatus(Status.NEW);
        taskManager.updateSubtask(subtask1);
        subtask2.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask2);

        assertSame(epic.getStatus(), Status.IN_PROGRESS);
    }

    @Test
    public void updateStatusSubtasksDoneAndInProgressEpicInProgress() {
        subtask1.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);
        subtask2.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask2);

        assertSame(epic.getStatus(), Status.IN_PROGRESS);
    }

    @Test
    public void updateStatusDeleteAllSubtaskEpicIsDone() {
        taskManager.deleteSubtask(subtask1.getId());
        taskManager.deleteSubtask(subtask2.getId());
        taskManager.deleteSubtask(subtask3.getId());

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
}