package ru.practicum.javakanban.manager;

import ru.practicum.javakanban.exeptions.ManagerPrioritizeException;
import ru.practicum.javakanban.exeptions.NotFoundException;
import ru.practicum.javakanban.model.Epic;
import ru.practicum.javakanban.model.Status;
import ru.practicum.javakanban.model.Subtask;
import ru.practicum.javakanban.model.Task;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager;
    private final Comparator<Task> comparator = Comparator.comparing(Task::getStartTime);
    private final Set<Task> prioritizedTasks = new TreeSet<>(comparator);
    private int idCounter = 1;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return prioritizedTasks.stream().toList();
    }

    protected boolean validateTimes(Task task1, Task task2) {
        if (task1.getStartTime() == null || task2.getStartTime() == null) {
            return true;
        } else return (!task1.getStartTime().isBefore(task2.getStartTime()) ||
                !task1.getEndTime().isAfter(task2.getStartTime())) &&
                (!task1.getStartTime().isBefore(task2.getEndTime()) ||
                        !task1.getEndTime().isAfter(task2.getEndTime())) &&
                (!task1.getStartTime().isBefore(task2.getStartTime()) ||
                        !task1.getEndTime().isAfter(task2.getEndTime())) &&
                (!task1.getStartTime().isAfter(task2.getStartTime()) ||
                        !task1.getEndTime().isBefore(task2.getEndTime())) &&
                !task1.getStartTime().equals(task2.getStartTime());
    }


    protected boolean validateTask(Task task) {
        if ((task.getStartTime() != null) && (!getPrioritizedTasks().isEmpty())) {
            return getPrioritizedTasks().stream()
                    .filter(someTask -> !someTask.equals(task))
                    .allMatch(someTask -> validateTimes(task, someTask));
        }

        return true;
    }

    protected void addPrioritizedTasks(Task task) throws ManagerPrioritizeException {
        if (task.getStartTime() == null) {
            return;
        }

        if (validateTask(task)) {
            prioritizedTasks.add(task);
        } else {
            throw new ManagerPrioritizeException("Задача не может быть создана/обновлена. Это время уже занято " +
                    "другой задачей.");
        }
    }

    @Override
    public void createTask(Task task) throws ManagerPrioritizeException {
        if (task.getStartTime() != null) {
            addPrioritizedTasks(task);
            task.setId(idCounter++);
            task.setStatus(Status.NEW);
            tasks.put(task.getId(), task);
        } else {
            throw new IllegalArgumentException("Задача не может быть создана без даты начала");
        }
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(idCounter++);
        epics.put(epic.getId(), epic);
    }

    @Override
    public void createSubtask(Subtask subtask, int epicId) throws ManagerPrioritizeException {
        if (subtask.getStartTime() != null) {
            addPrioritizedTasks(subtask);
            subtask.setId(idCounter++);
            subtask.setStatus(Status.NEW);
            Epic epic = epics.get(epicId);

            List<Subtask> epicSubtasks = epic.getSubtasks();
            epicSubtasks.add(subtask);

            subtask.setEpicId(epic.getId());
            subtasks.put(subtask.getId(), subtask);

            epic.updateStatus();
            updateEpic(epic, epicId);
        } else {
            throw new IllegalArgumentException("Задача не может быть добавлена без даты начала");
        }
    }

    @Override
    public void updateTask(Task newTask, Integer id) throws ManagerPrioritizeException {
        if (!tasks.containsKey(id)) {
            throw new IllegalArgumentException("Задача для изменения не найдена.");
        }

        if (newTask.getStartTime() != null) {
            newTask.setId(id);
            addPrioritizedTasks(newTask);

            deleteTask(id);

            tasks.put(newTask.getId(), newTask);
        } else {
            throw new IllegalArgumentException("Задача не может быть изменена без даты начала");
        }
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
    public void updateSubtask(Subtask newSubtask, Integer id) throws ManagerPrioritizeException {
        if (newSubtask.getStartTime() != null) {
            newSubtask.setId(id);
            addPrioritizedTasks(newSubtask);

            newSubtask.setEpicId(subtasks.get(id).getEpicId());
            deleteSubtask(id);

            createSubtaskForUpdate(newSubtask, newSubtask.getEpicId());
        } else {
            throw new IllegalArgumentException("Задача не может быть изменена без даты начала");
        }
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
    public List<Subtask> getEpicSubtasks(int id) throws NotFoundException {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            return epic.getSubtasks();
        } else {
            throw new NotFoundException("Эпик не найден по id.");
        }
    }

    @Override
    public void deleteAllTasks() {

        List<Task> tasksForDelete = new ArrayList<>(tasks.values());

        tasksForDelete.stream().forEach(task -> deleteTask(task.getId()));

        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        List<Epic> epicsForDelete = new ArrayList<>(epics.values());
        epicsForDelete.stream().forEach(epic -> {
            try {
                deleteEpic(epic.getId());
            } catch (NotFoundException e) {
                System.out.println("Внимание! Рак на горе свистнул!");
            }
        });
    }

    @Override
    public void deleteAllSubtasks() {
        List<Epic> allEpics = getAllEpics();
        allEpics.stream().forEach(epic -> {
            epic.clearSubtasks();
            epic.updateStatus();
            updateEpic(epic, epic.getId());
        });

        List<Subtask> subtasksForDelete = new ArrayList<>(subtasks.values());
        subtasksForDelete.stream().forEach(subtask -> deleteSubtask(subtask.getId()));
    }

    @Override
    public Task getTask(int id) throws NotFoundException {
        if (tasks.containsKey(id)) {
            historyManager.add(tasks.get(id));
            return tasks.get(id);
        } else {
            throw new NotFoundException("Задача не найдена по id.");
        }
    }

    @Override
    public Epic getEpic(int id) throws NotFoundException {
        if (epics.containsKey(id)) {
            historyManager.add(epics.get(id));
            return epics.get(id);
        } else {
            throw new NotFoundException("Эпик не найден по id");
        }
    }

    @Override
    public Subtask getSubtask(int id) throws NotFoundException {
        if (subtasks.containsKey(id)) {
            historyManager.add(subtasks.get(id));
            return subtasks.get(id);
        } else {
            throw new NotFoundException("Подзадача не найдена по id");
        }
    }

    @Override
    public void deleteTask(int id) {
        if (tasks.containsKey(id)) {
            historyManager.remove(id);
            if (tasks.get(id).getStartTime() != null) {
                prioritizedTasks.remove(tasks.get(id));
            }
            tasks.remove(id);
        } else {
            throw new IllegalArgumentException("Задача для удаления не найдена");
        }
    }

    @Override
    public void deleteEpic(int id) throws NotFoundException {

        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            List<Subtask> epicSubtasks = epic.getSubtasks();

            epicSubtasks.stream().forEach(subtask -> {
                if (subtask.getStartTime() != null) {
                    prioritizedTasks.remove(subtask);
                }

                historyManager.remove(subtask.getId());
                subtasks.remove(subtask.getId());
            });

            historyManager.remove(id);
            epics.remove(id);
        } else {
            throw new NotFoundException("Эпик для удаления не найден");
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
            if (subtasks.get(id).getStartTime() != null) {
                prioritizedTasks.remove(subtask);
            }

            subtasks.remove(id);
            updateEpic(epic, epic.getId());
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public Map<Integer, Task> getTasks() {
        return tasks;
    }

    @Override
    public Map<Integer, Epic> getEpics() {
        return epics;
    }

    @Override
    public Map<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    protected void createSubtaskForUpdate(Subtask subtask, int epicId) {
        Epic epic = epics.get(epicId);

        List<Subtask> epicSubtasks = epic.getSubtasks();
        epicSubtasks.add(subtask);

        subtask.setEpicId(epic.getId());
        subtasks.put(subtask.getId(), subtask);

        epic.updateStatus();
        updateEpic(epic, epicId);
    }
}