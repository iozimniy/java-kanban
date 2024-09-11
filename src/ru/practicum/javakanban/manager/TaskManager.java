package ru.practicum.javakanban.manager;

import ru.practicum.javakanban.exeptions.ManagerPrioritizeException;
import ru.practicum.javakanban.exeptions.NotFoundException;
import ru.practicum.javakanban.model.Epic;
import ru.practicum.javakanban.model.Subtask;
import ru.practicum.javakanban.model.Task;

import java.util.List;
import java.util.Map;

public interface TaskManager {

    void createTask(Task task) throws ManagerPrioritizeException;

    void createEpic(Epic epic);

    void createSubtask(Subtask subtask, int epicId) throws ManagerPrioritizeException;

    void updateTask(Task task, Integer id) throws ManagerPrioritizeException;

    void updateEpic(Epic epic, Integer id);

    void updateSubtask(Subtask subtask, Integer id) throws ManagerPrioritizeException;

    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<Subtask> getAllSubtasks();

    List<Subtask> getEpicSubtasks(int id) throws NotFoundException;

    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubtasks();

    Task getTask(int id) throws NotFoundException;

    Epic getEpic(int id) throws NotFoundException;

    Subtask getSubtask(int id);

    void deleteTask(int id);

    void deleteEpic(int id) throws NotFoundException;

    void deleteSubtask(int id);

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();

    Map<Integer, Task> getTasks();

    Map<Integer, Epic> getEpics();

    Map<Integer, Subtask> getSubtasks();
}
