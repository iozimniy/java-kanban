package ru.practicum.javakanban.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.javakanban.manager.Managers;
import ru.practicum.javakanban.manager.TaskManager;

import static org.junit.jupiter.api.Assertions.assertSame;

class EpicTest {

    static TaskManager taskManager;
    Epic epic;
    Subtask subtask1;
    Subtask subtask2;

    @BeforeEach
    public void createTestEpic() {
        taskManager = Managers.getDefault();
        epic = new Epic("Эпик", "Описание эпика");
        subtask1 = new Subtask("Подзадача № 1", "Описание подзадачи № 1");
        subtask2 = new Subtask("Подзадача № 2", "Описание подзадачи № 2");
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask1, epic.getId());
        taskManager.createSubtask(subtask2, epic.getId());
    }


    @Test
    public void updateStatusAllSubtaskIsDoneEpicIsDone() {
        subtask1.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);
        subtask2.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask2);

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

        assertSame(epic.getStatus(), Status.DONE);
    }
}