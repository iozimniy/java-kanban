package ru.practicum.javakanban.manager;

import ru.practicum.javakanban.model.Epic;
import ru.practicum.javakanban.model.Subtask;
import ru.practicum.javakanban.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager;
    private int idCounter = 0;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public void createTask(Task task) {
        task.setId(idCounter++);
        tasks.put(task.getId(), task);
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(idCounter++);
        epics.put(epic.getId(), epic);
    }

    @Override
    public void createSubtask(Subtask subtask, int epicId) {
        subtask.setId(idCounter++);
        Epic epic = epics.get(epicId);

        List<Subtask> epicSubtasks = epic.getSubtasks();
        epicSubtasks.add(subtask);

        subtask.setEpicId(epic.getId());
        subtasks.put(subtask.getId(), subtask);

        epic.updateStatus();
        updateEpic(epic);
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());

        epic.updateStatus();
        updateEpic(epic);
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Subtask> getEpicSubtasks(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            return epic.getSubtasks();
        }

        return null;
    }

    @Override
    public void deleteAllTasks() {
        for (Task task : tasks.values()) {
            deleteTask(task.getId());
        }

        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {

        for (Epic epic : epics.values()) {
            deleteEpic(epic.getId());
        }
    }

    @Override
    public void deleteAllSubtasks() {

        for (Epic epic : epics.values()) {
            epic.clearSubtasks();
            epic.updateStatus();
            updateEpic(epic);
        }

        for (Subtask subtask : subtasks.values()) {
            deleteSubtask(subtask.getId());
        }
    }

    @Override
    public Task getTask(int id) {
        if (tasks.containsKey(id)) {
            historyManager.add(tasks.get(id));
            return tasks.get(id);
        }

        return null;
    }

    @Override
    public Epic getEpic(int id) {
        if (epics.containsKey(id)) {
            historyManager.add(epics.get(id));
            return epics.get(id);
        }

        return null;
    }

    @Override
    public Subtask getSubtask(int id) {
        if (subtasks.containsKey(id)) {
            historyManager.add(subtasks.get(id));
            return subtasks.get(id);
        }

        return null;
    }

    @Override
    public void deleteTask(int id) {
        Task task = tasks.get(id);

        if (historyManager.getHistory().contains(task)) {
            historyManager.remove(id);
        }

        tasks.remove(id);
    }

    @Override
    public void deleteEpic(int id) {

        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            List<Subtask> epicSubtasks = epic.getSubtasks();

            for (Subtask subtask : epicSubtasks) {
                if (historyManager.getHistory().contains(subtask)) {
                    historyManager.remove(subtask.getId());
                }

                subtasks.remove(subtask.getId());
            }

            if (historyManager.getHistory().contains(epic)) {
                historyManager.remove(id);
            }

            epics.remove(id);
        }
    }

    @Override
    public void deleteSubtask(int id) {
        if (subtasks.containsKey(id)) {
            Subtask subtask = subtasks.get(id);
            Epic epic = epics.get(subtask.getEpicId());

            epic.removeSubtask(subtask);
            epic.updateStatus();

            if (historyManager.getHistory().contains(subtask)){
                historyManager.remove(id);
            }

            subtasks.remove(id);
            updateEpic(epic);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    public Map<Integer, Task> getTasks() {
        return tasks;
    }

    public Map<Integer, Epic> getEpics() {
        return epics;
    }

    public Map<Integer, Subtask> getSubtasks() {
        return subtasks;
    }
}