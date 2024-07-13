package ru.practicum.javakanban.manager;

import ru.practicum.javakanban.exeptions.ManagerSaveException;
import ru.practicum.javakanban.model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File taskManagerCsv;

    public FileBackedTaskManager(HistoryManager historyManager, File taskManagerCsv) {
        super(historyManager);
        this.taskManagerCsv = taskManagerCsv;
    }

    private static Task fromString(String value) {
        String[] parameters = value.split(",");

        return switch (parameters[1]) {
            case "TASK" -> new Task(parameters[2], parameters[4], Integer.parseInt(parameters[0]),
                    Status.fromString(parameters[3]), TaskType.TASK);
            case "SUBTASK" -> new Subtask(parameters[2], parameters[4], Integer.parseInt(parameters[0]),
                    Status.fromString(parameters[3]), TaskType.SUBTASK, Integer.parseInt(parameters[5]));
            case "EPIC" -> new Epic(parameters[2], parameters[4], Integer.parseInt(parameters[0]),
                    Status.fromString(parameters[3]), TaskType.EPIC);
            default -> throw new IllegalArgumentException("Невалидная строка.");
        };
    }

    private static List<String> getStrings(File file) {
        List<String> stringTasks = new ArrayList<>();

        try (Reader reader = new FileReader(file.getAbsolutePath(), StandardCharsets.UTF_8)) {
            BufferedReader bufferedReader = new BufferedReader(reader);

            while (bufferedReader.ready()) {
                String taskStr = bufferedReader.readLine();
                stringTasks.add(taskStr);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Внимание: рак на горе свистнул!");
        }

        return stringTasks;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(Managers.getDefaultHistory(), file);

        var strings = getStrings(file);

        for (String string : strings) {
            Task task = fromString(string);
            switch (task.getType()) {
                case TASK -> fileBackedTaskManager.createTask(task);
                case EPIC -> fileBackedTaskManager.createEpic((Epic) task);
                case SUBTASK -> fileBackedTaskManager.createSubtask((Subtask) task, ((Subtask) task).getEpicId());
            }
        }

        return fileBackedTaskManager;
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask, int epicId) {
        super.createSubtask(subtask, epicId);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    private void save() {
        List<Task> tasks = getAllTasks();
        List<Epic> epics = getAllEpics();
        List<Subtask> subtasks = getAllSubtasks();

        try (Writer writer = new FileWriter(taskManagerCsv, StandardCharsets.UTF_8)) {

            for (Task task : tasks) {
                writer.write(task.convertToString() + "\n");
            }

            for (Epic epic : epics) {
                writer.write(epic.convertToString() + "\n");
            }

            for (Subtask subtask : subtasks) {
                writer.write(subtask.convertToString() + "\n");
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Внимание: рак на горе свистнул!");
        }
    }

    public File getTaskManagerCsv() {
        return taskManagerCsv;
    }
}
