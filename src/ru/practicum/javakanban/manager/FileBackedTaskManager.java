package ru.practicum.javakanban.manager;

import ru.practicum.javakanban.exeptions.ManagerLoadException;
import ru.practicum.javakanban.exeptions.ManagerPrioritizeException;
import ru.practicum.javakanban.exeptions.ManagerSaveException;
import ru.practicum.javakanban.exeptions.NotFoundException;
import ru.practicum.javakanban.model.Epic;
import ru.practicum.javakanban.model.Status;
import ru.practicum.javakanban.model.Subtask;
import ru.practicum.javakanban.model.Task;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileBackedTaskManager extends InMemoryTaskManager {

    public static final String HEADER = "id,type,name,status,description,duration,startTime,epicId";
    private final File taskManagerCsv;

    public FileBackedTaskManager(HistoryManager historyManager, File taskManagerCsv) {
        super(historyManager);
        this.taskManagerCsv = taskManagerCsv;
    }

    private static Task fromString(String value) {
        String[] headers = HEADER.split(",");
        Map<String, Integer> params = new HashMap<>();

        for (int i = 0; i < headers.length; i++) {
            params.put(headers[i], i);
        }

        String[] parameters = value.split(",");

        return switch (parameters[params.get("type")]) {
            case "TASK" -> new Task(parameters[params.get("name")], parameters[params.get("description")],
                    Integer.parseInt(parameters[params.get("id")]), Status.fromString(parameters[params.get("status")]),
                    Duration.ofMinutes(Long.parseLong(parameters[params.get("duration")])),
                    LocalDateTime.parse(parameters[params.get("startTime")]));
            case "SUBTASK" ->
                    new Subtask(parameters[params.get("name")], parameters[params.get("description")], Integer.parseInt(parameters[params.get("id")]),
                            Status.fromString(parameters[params.get("status")]), Integer.parseInt(parameters[params.get("epicId")]), Duration.ofMinutes(Long.parseLong(parameters[params.get("duration")])),
                            LocalDateTime.parse(parameters[params.get("startTime")]));
            case "EPIC" ->
                    new Epic(parameters[params.get("name")], parameters[params.get("description")], Integer.parseInt(parameters[params.get("id")]),
                            Status.fromString(parameters[params.get("status")]));
            default -> throw new IllegalArgumentException("Невалидная строка.");
        };
    }

    private static List<String> getStrings(File file) {
        List<String> stringTasks = new ArrayList<>();

        try (Reader reader = new FileReader(file.getAbsolutePath(), StandardCharsets.UTF_8)) {
            BufferedReader bufferedReader = new BufferedReader(reader);
            boolean firstString = true;

            while (bufferedReader.ready()) {
                if (firstString) {
                    bufferedReader.readLine();
                    firstString = false;
                    continue;
                }
                String taskStr = bufferedReader.readLine();
                stringTasks.add(taskStr);
            }
        } catch (IOException e) {
            throw new ManagerLoadException("Внимание: рак на горе свистнул!");
        }

        return stringTasks;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(Managers.getDefaultHistory(), file);
        var strings = getStrings(file);

        try {
            for (String string : strings) {
                Task task = fromString(string);
                switch (task.getType()) {
                    case TASK -> fileBackedTaskManager.createTask(task);
                    case EPIC -> fileBackedTaskManager.createEpic((Epic) task);
                    case SUBTASK -> fileBackedTaskManager.createSubtask((Subtask) task, ((Subtask) task).getEpicId());
                }
            }
        } catch (ManagerPrioritizeException e) {
            throw new ManagerSaveException("Ошибка чтения из файла.");
        }

        return fileBackedTaskManager;
    }

    @Override
    public void createTask(Task task) throws ManagerPrioritizeException {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask, Integer epicId) throws ManagerPrioritizeException {
        super.createSubtask(subtask, epicId);
        save();
    }

    @Override
    public void updateTask(Task task, Integer id) throws ManagerPrioritizeException {
        super.updateTask(task, id);
        save();
    }

    @Override
    public void updateEpic(Epic epic, Integer id) {
        super.updateEpic(epic, id);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask, Integer id) throws ManagerPrioritizeException {
        super.updateSubtask(subtask, id);
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
    public void deleteEpic(int id) throws NotFoundException {
        super.deleteEpic(id);
        save();
    }

    private void save() {
        List<Task> tasks = getAllTasks();
        List<Epic> epics = getAllEpics();
        List<Subtask> subtasks = getAllSubtasks();

        try (Writer writer = new FileWriter(taskManagerCsv, StandardCharsets.UTF_8)) {

            writer.write(HEADER + "\n");

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
