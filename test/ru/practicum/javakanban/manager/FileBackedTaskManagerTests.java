package ru.practicum.javakanban.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.javakanban.model.Epic;
import ru.practicum.javakanban.model.Subtask;
import ru.practicum.javakanban.model.Task;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    private final String resources = ".\\src\\resources";
    private FileBackedTaskManager fileBackedTaskManager;
    private Task task;
    private Epic epic;
    private Subtask subtask;

    @BeforeEach
    public void createFileBackedTaskManager() {
        /*здесь также можно использовать метод createFile(), который создаст файл в папке resources, что может быть
        удобнее для отладки*/
        fileBackedTaskManager = new FileBackedTaskManager(Managers.getDefaultHistory(), createTempFile());
    }

    @Test
    public void saveEmptyFileManagerHasNoTasks() {
        File file = createTempFile();
        FileBackedTaskManager newFileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);

        List<Task> tasks = newFileBackedTaskManager.getAllTasks();
        List<Epic> epics = newFileBackedTaskManager.getAllEpics();
        List<Subtask> subtasks = newFileBackedTaskManager.getAllSubtasks();

        assertAll(
                () -> assertTrue(tasks.isEmpty()),
                () -> assertTrue(epics.isEmpty()),
                () -> assertTrue(subtasks.isEmpty())
        );
    }

    @Test
    public void createTaskFileWithTask() {
        createTestTask();

        String taskString = null;
        try {
            taskString = Files.readString(fileBackedTaskManager.getTaskManagerCsv().toPath()).trim();
        } catch (IOException e) {
            System.out.println("Не удалось прочитать строку из файла");
        }

        assertEquals(taskString, task.convertToString(), "Задача не записалась в файл");
    }

    @Test
    public void createEpicFileWithEpic() {
        createTestEpic();

        String epicString = null;

        try {
            epicString = Files.readString(fileBackedTaskManager.getTaskManagerCsv().toPath()).trim();
        } catch (IOException e) {
            System.out.println("Не удалось прочитать строку из файла");
        }

        assertEquals(epicString, epic.convertToString(), "Эпик не записался в файл");
    }

    @Test
    public void createSubtaskFileWithEpicAndSubtask() {
        createTestSubtask();

        String file = fileBackedTaskManager.getTaskManagerCsv().getAbsolutePath();
        String epicString = null;
        String subtaskString = null;

        try (Reader reader = new FileReader(file, UTF_8)) {
            BufferedReader bufferedReader = new BufferedReader(reader);
            epicString = bufferedReader.readLine();
            subtaskString = bufferedReader.readLine();
        } catch (IOException e) {
            System.out.println("Не удалось прочитать строку из файла.");
        }

        assertEquals(epicString, epic.convertToString(), "Эпик не записался в файл");
        assertEquals(subtaskString, subtask.convertToString(), "Подзадача не записался в файл");
    }

    @Test
    public void returnTaskFromFile() {
        createTestTask();
        File file = fileBackedTaskManager.getTaskManagerCsv();
        FileBackedTaskManager newFileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);
        assertEquals(newFileBackedTaskManager.getTask(task.getId()), task, "Задачу не удалось восстановить " +
                "из файла");
    }

    @Test
    public void returnEpicFromFile() {
        createTestEpic();
        File file = fileBackedTaskManager.getTaskManagerCsv();
        FileBackedTaskManager newFileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);
        assertEquals(newFileBackedTaskManager.getEpic(epic.getId()), epic, "Эпик не удалось восстановить " +
                "из файла");
    }

    @Test
    public void returnSubtaskAndEpicFromFile() {
        createTestSubtask();
        File file = fileBackedTaskManager.getTaskManagerCsv();
        FileBackedTaskManager newFileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);
        assertAll(
                () -> assertEquals(newFileBackedTaskManager.getSubtask(subtask.getId()), subtask, "Подзадачу не " +
                        "удалось восстановить из файла"),
                () -> assertEquals(newFileBackedTaskManager.getEpic(epic.getId()), epic, "Эпик не удалось восстановить " +
                        "из файла")
        );
    }

    @Test
    public void updateTaskUpdateInFile() {
        createTestTask();
        task.setName("Новое название задачи");
        fileBackedTaskManager.updateTask(task);
        String taskString = null;

        try {
            taskString = Files.readString(fileBackedTaskManager.getTaskManagerCsv().toPath()).trim();
        } catch (IOException e) {
            System.out.println("Не удалось прочитать строку из файла.");
        }

        assertEquals(task.convertToString(), taskString, "Задача не обновилась в файле");
    }

    @Test
    public void updateEpicUpdateInFile() {
        createTestEpic();
        epic.setName("Новое название эпика");
        fileBackedTaskManager.updateEpic(epic);
        String epicString = null;

        try {
            epicString = Files.readString(fileBackedTaskManager.getTaskManagerCsv().toPath()).trim();
        } catch (IOException e) {
            System.out.println("Не удалось прочитать строку из файла");
        }

        assertEquals(epic.convertToString(), epicString, "Эпик не обновился в файле");
    }

    @Test
    public void updateSubtaskUpdateInFile() {
        createTestSubtask();
        subtask.setName("Новое название подзадачи");
        fileBackedTaskManager.updateSubtask(subtask);

        String file = fileBackedTaskManager.getTaskManagerCsv().getAbsolutePath();
        String epicString = null;
        String subtaskString = null;

        try (Reader reader = new FileReader(file, UTF_8)) {
            BufferedReader bufferedReader = new BufferedReader(reader);
            epicString = bufferedReader.readLine();
            subtaskString = bufferedReader.readLine();
        } catch (IOException e) {
            System.out.println("Не удалось прочитать строку из файла!");
        }

        assertEquals(epicString, epic.convertToString(), "Эпик не записался в файл");
        assertEquals(subtask.convertToString(), subtaskString, "Подзадача не обновилась в файле");
    }

    @Test
    public void deleteTaskFileIsEmpty() {
        createTestTask();
        fileBackedTaskManager.deleteTask(task.getId());

        String emptyString = null;

        try {
            emptyString = Files.readString(fileBackedTaskManager.getTaskManagerCsv().toPath()).trim();
        } catch (IOException e) {
            System.out.println("Не удалось прочитать строку из файла");
        }

        assertTrue(emptyString.isEmpty(), "Строка не пустая после удаления задачи");
    }

    @Test
    public void deleteEpicFileIsEmpty() {
        createTestEpic();
        fileBackedTaskManager.deleteEpic(epic.getId());

        String emptyString = null;

        try {
            emptyString = Files.readString(fileBackedTaskManager.getTaskManagerCsv().toPath()).trim();
        } catch (IOException e) {
            System.out.println("Не удалось прочитать строку из файла");
        }

        assertTrue(emptyString.isEmpty(), "Строка не пустая после удаления эпика");
    }

    @Test
    public void deleteSubtaskEpicInFile() {
        createTestSubtask();
        fileBackedTaskManager.deleteSubtask(subtask.getId());

        String file = fileBackedTaskManager.getTaskManagerCsv().getAbsolutePath();
        String epicString = null;
        String subtaskString = null;

        try (Reader reader = new FileReader(file, UTF_8)) {
            BufferedReader bufferedReader = new BufferedReader(reader);
            epicString = bufferedReader.readLine();
            subtaskString = bufferedReader.readLine();
        } catch (IOException e) {
            System.out.println("Ну удалось прочитать строку из файла");
        }

        assertEquals(epicString, epic.convertToString(), "Эпик не записался в файл");
        assertNull(subtaskString, "Подзадача после удаления не удалилась из файла");
    }

    @Test
    public void deleteAllTasksFileIsEmpty() {
        createTestTask();
        createTestTask();
        fileBackedTaskManager.deleteAllTasks();

        String emptyString = null;

        try {
            emptyString = Files.readString(fileBackedTaskManager.getTaskManagerCsv().toPath()).trim();
        } catch (IOException e) {
            System.out.println("Не удалось прочитать строку из файла");
        }

        assertTrue(emptyString.isEmpty(), "Строка не пустая после удаления задач");
    }

    @Test
    public void deleteAllEpicsFileIsEmpty() {
        createTestEpic();
        createTestEpic();
        fileBackedTaskManager.deleteAllEpics();

        String emptyString = null;

        try {
            emptyString = Files.readString(fileBackedTaskManager.getTaskManagerCsv().toPath()).trim();
        } catch (IOException e) {
            System.out.println("Не удалось прочитать строку из файла");
        }

        assertTrue(emptyString.isEmpty(), "Строка не пустая после удаления задач");
    }

    @Test
    public void deleteAllSubtasksEpicInFile() {
        createTestSubtask();
        Subtask subtask1 = new Subtask("Подзадача", "Описание подзадачи");
        fileBackedTaskManager.createSubtask(subtask1, epic.getId());
        fileBackedTaskManager.deleteAllSubtasks();

        String file = fileBackedTaskManager.getTaskManagerCsv().getAbsolutePath();
        List<String> stringTasksFromFile = new ArrayList<>();

        try (Reader reader = new FileReader(file, UTF_8)) {
            BufferedReader bufferedReader = new BufferedReader(reader);
            while (bufferedReader.ready()) {
                String str = bufferedReader.readLine();
                stringTasksFromFile.add(str);
            }
        } catch (IOException e) {
            System.out.println("Не удалось прочитать строку из файла");
        }

        assertAll(
                () -> assertEquals(stringTasksFromFile.getFirst(), epic.convertToString()),
                () -> assertEquals(1, stringTasksFromFile.size())
        );
    }

    public File createFile() {
        File taskManagerCsvFile = null;
        try {
            if (!Files.exists(Paths.get(resources, "taskManagerCsv.csv"))) {
                Files.createDirectory(Paths.get(resources));
                Files.createFile(Paths.get(resources, "taskManagerCsv.csv"));
            }

            taskManagerCsvFile = Paths.get(resources, "taskManagerCsv.csv").toFile();

        } catch (IOException e) {
            System.out.println("Не удалось создать файл.");
        }

        return taskManagerCsvFile;
    }

    public File createTempFile() {
        File file = null;

        try {
            file = File.createTempFile("taskManagerCsv", ".csv");
        } catch (IOException e) {
            System.out.println("Не удалось создать файл.");
        }

        return file;
    }

    //вспомогательные методы
    private void createTestTask() {
        task = new Task("Задача", "Описание задачи");
        fileBackedTaskManager.createTask(task);
    }

    private void createTestEpic() {
        epic = new Epic("Эпик", "Описание эпика");
        fileBackedTaskManager.createEpic(epic);
    }

    private void createTestSubtask() {
        epic = new Epic("Эпик", "Описание эпика");
        fileBackedTaskManager.createEpic(epic);
        subtask = new Subtask("Подзадача", "Описание подзадачи");
        fileBackedTaskManager.createSubtask(subtask, epic.getId());
    }
}
