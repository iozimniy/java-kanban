package ru.practicum.javakanban.manager;

import ru.practicum.javakanban.model.Epic;
import ru.practicum.javakanban.model.Subtask;
import ru.practicum.javakanban.model.Task;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager;
    private final Comparator<Task> comparator = Comparator.comparing(task -> task.getStartTime());
    private final Set<Task> prioritizedTasks = new TreeSet<>(comparator);
    private int idCounter = 0;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    public List<Task> getPrioritizedTasks() {
        return prioritizedTasks.stream().toList();
    }

    public boolean validateTimes(Task task1, Task task2) {
        if (task1.getStartTime() == null || task2.getStartTime() == null) {
            return true;
        } else if ((task1.getStartTime().isBefore(task2.getStartTime()) &&
                task1.getEndTime().isAfter(task2.getStartTime())) ||
                (task1.getStartTime().isBefore(task2.getEndTime()) &&
                task1.getEndTime().isAfter(task2.getEndTime())) ||
                (task1.getStartTime().isBefore(task2.getStartTime()) &&
                        task1.getEndTime().isAfter(task2.getEndTime())) ||
                (task1.getStartTime().isAfter(task2.getStartTime()) &&
                        task1.getEndTime().isBefore(task2.getEndTime())) ||
                task1.getStartTime().equals(task2.getStartTime())){
            return false;
        }

        return true;
    }

    @Override
    public void createTask(Task task) {
        task.setId(idCounter++);
        prioritizedTasks.add(task);
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
        prioritizedTasks.add(subtask);
        subtasks.put(subtask.getId(), subtask);

        epic.updateStatus();
        updateEpic(epic, epicId);
    }

    @Override
    public void updateTask(Task newTask, Integer id) {
        deleteTask(id);
        newTask.setId(id);

        tasks.put(newTask.getId(), newTask);
        prioritizedTasks.add(newTask);
    }

    @Override
    public void updateEpic(Epic newEpic, Integer id) {
        newEpic.setSubtasks(epics.get(id).getSubtasks());
        epics.remove(id);

        newEpic.setId(id);
        epics.put(newEpic.getId(), newEpic);
        newEpic.updateTimes();
    }

    @Override
    public void updateSubtask(Subtask newSubtask, Integer id) {
        newSubtask.setEpicId(subtasks.get(id).getEpicId());

        deleteSubtask(id);
        newSubtask.setId(id);

        createSubtaskForUpdate(newSubtask, newSubtask.getEpicId());
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

        List<Task> tasksForDelete = new ArrayList<>(tasks.values());

        for (Task task : tasksForDelete) {
            deleteTask(task.getId());
        }

        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {

        List<Epic> epicsForDelete = new ArrayList<>(epics.values());

        for (Epic epic : epicsForDelete) {
            deleteEpic(epic.getId());
        }
    }

    @Override
    public void deleteAllSubtasks() {

        for (Epic epic : epics.values()) {
            epic.clearSubtasks();
            epic.updateStatus();
            updateEpic(epic, epic.getId());
        }

        List<Subtask> subtasksForDelete = new ArrayList<>(subtasks.values());

        for (Subtask subtask : subtasksForDelete) {
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
        if (tasks.containsKey(id)) {
            historyManager.remove(id);
            prioritizedTasks.remove(tasks.get(id));
            tasks.remove(id);
        }
    }

    @Override
    public void deleteEpic(int id) {

        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            List<Subtask> epicSubtasks = epic.getSubtasks();

            for (Subtask subtask : epicSubtasks) {
                historyManager.remove(subtask.getId());
                subtasks.remove(subtask.getId());
            }

            historyManager.remove(id);
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
            historyManager.remove(id);
            prioritizedTasks.remove(subtask);

            subtasks.remove(id);
            updateEpic(epic, epic.getId());
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

    public void createSubtaskForUpdate(Subtask subtask, int epicId) {
        Epic epic = epics.get(epicId);

        List<Subtask> epicSubtasks = epic.getSubtasks();
        epicSubtasks.add(subtask);

        subtask.setEpicId(epic.getId());
        prioritizedTasks.add(subtask);
        subtasks.put(subtask.getId(), subtask);

        epic.updateStatus();
        updateEpic(epic, epicId);
    }
}