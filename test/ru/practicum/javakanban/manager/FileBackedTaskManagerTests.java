package ru.practicum.javakanban.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.javakanban.exeptions.ManagerPrioritizeException;
import ru.practicum.javakanban.exeptions.NotFoundException;
import ru.practicum.javakanban.model.Epic;
import ru.practicum.javakanban.model.Subtask;
import ru.practicum.javakanban.model.Task;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends ManagersTest {
    private final String resources = ".\\src\\resources";
    protected FileBackedTaskManager fileBackedTaskManager;
    private Task task;
    private Epic epic;
    private Subtask subtask;
    private static LocalDateTime TASKS_DATE_TIME = LocalDateTime.of(2024,12,31,12,30);
    private static Duration TASKS_DURATION = Duration.ofMinutes(30);

    @Override
    @BeforeEach
    public void createTaskManager() throws IOException {
        /*здесь также можно использовать метод createFile(), который создаст файл в папке resources, что может быть
        удобнее для отладки*/
        taskManager = Managers.getFileBacked(createFile());
        fileBackedTaskManager = Managers.getFileBacked(createTempFile());
    }



    @Test
    public void saveEmptyFileManagerHasNoTasks() throws IOException {
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
    public void createTaskFileWithTask() throws IOException, ManagerPrioritizeException {
        createTestTask();

        String file = fileBackedTaskManager.getTaskManagerCsv().getAbsolutePath();
        String taskFromFile = getFirsNote(file);

        assertEquals(task.convertToString(), taskFromFile, "Задача не записалась в файл");
    }

    @Test
    public void createEpicFileWithEpic() throws IOException {
        createTestEpic();

        String file = fileBackedTaskManager.getTaskManagerCsv().getAbsolutePath();
        String taskFromFile = getFirsNote(file);

        assertEquals(epic.convertToString(), taskFromFile, "Эпик не записался в файл");
    }

    @Test
    public void createSubtaskFileWithEpicAndSubtask() throws IOException, ManagerPrioritizeException, NotFoundException {
        createTestSubtask();

        String file = fileBackedTaskManager.getTaskManagerCsv().getAbsolutePath();

        String epicString = getFirsNote(file);
        String subtaskString = getSecondNote(file);

        assertEquals(epicString, epic.convertToString(), "Эпик не записался в файл");
        assertEquals(subtaskString, subtask.convertToString(), "Подзадача не записался в файл");
    }

    @Test
    public void returnTaskFromFile() throws ManagerPrioritizeException, NotFoundException {
        createTestTask();
        File file = fileBackedTaskManager.getTaskManagerCsv();
        FileBackedTaskManager newFileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);

        assertEquals(task, newFileBackedTaskManager.getTask(task.getId()), "Задачу не удалось восстановить " +
                "из файла");
    }

    @Test
    public void returnEpicFromFile() throws NotFoundException {
        createTestEpic();
        File file = fileBackedTaskManager.getTaskManagerCsv();
        FileBackedTaskManager newFileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);

        assertEquals(newFileBackedTaskManager.getEpic(epic.getId()), epic, "Эпик не удалось восстановить " +
                "из файла");
    }

    @Test
    public void returnSubtaskAndEpicFromFile() throws ManagerPrioritizeException, NotFoundException {
        createTestSubtask();
        File file = fileBackedTaskManager.getTaskManagerCsv();
        FileBackedTaskManager newFileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);

        assertAll(
                () -> assertEquals(subtask, newFileBackedTaskManager.getSubtask(subtask.getId()), "Подзадачу не " +
                        "удалось восстановить из файла"),
                () -> assertEquals(epic, newFileBackedTaskManager.getEpic(epic.getId()), "Эпик не удалось восстановить " +
                        "из файла")
        );
    }

    @Test
    public void updateTaskUpdateInFile() throws IOException, ManagerPrioritizeException {
        createTestTask();

        Task updateTask = new Task("Новое название задачи", task.getDescription(), task.getStatus(),
                task.getDuration(), task.getStartTime());
        fileBackedTaskManager.updateTask(updateTask, task.getId());

        String file = fileBackedTaskManager.getTaskManagerCsv().getAbsolutePath();
        String taskString = getFirsNote(file);

        assertEquals(updateTask.convertToString(), taskString, "Задача не обновилась в файле");
    }

    @Test
    public void updateEpicUpdateInFile() throws IOException, NotFoundException {
        createTestEpic();
        Epic updateEpic = new Epic("Новое название эпика", epic.getDescription());
        fileBackedTaskManager.updateEpic(updateEpic, epic.getId());

        String file = fileBackedTaskManager.getTaskManagerCsv().getAbsolutePath();
        String taskString = getFirsNote(file);

        assertEquals(updateEpic.convertToString(), taskString, "Задача не обновилась в файле");
    }

    @Test
    public void updateSubtaskUpdateInFile() throws IOException, ManagerPrioritizeException, NotFoundException {
        createTestSubtask();

        Subtask updateSubtask = new Subtask("Новое название подзадачи", subtask.getDescription(),
                subtask.getStatus(), subtask.getDuration(), subtask.getStartTime());
        fileBackedTaskManager.updateSubtask(updateSubtask, subtask.getId());

        String file = fileBackedTaskManager.getTaskManagerCsv().getAbsolutePath();
        String epicString = getFirsNote(file);
        String subtaskString = getSecondNote(file);

        assertEquals(epic.convertToString(), epicString, "Эпик не записался в файл");
        assertEquals(updateSubtask.convertToString(), subtaskString, "Подзадача не обновилась в файле");
    }

    @Test
    public void deleteTaskFileIsEmpty() throws IOException, ManagerPrioritizeException {
        createTestTask();
        fileBackedTaskManager.deleteTask(task.getId());

        String file = fileBackedTaskManager.getTaskManagerCsv().getAbsolutePath();
        String nullString = getFirsNote(file);

        assertNull(nullString, "Есть вторая строка после удаления задачи");
    }

    @Test
    public void deleteEpicFileIsEmpty() throws IOException, NotFoundException {
        createTestEpic();
        fileBackedTaskManager.deleteEpic(epic.getId());

        String file = fileBackedTaskManager.getTaskManagerCsv().getAbsolutePath();
        String nullString = getFirsNote(file);

        assertNull(nullString, "Есть вторая строка после удаления эпика");
    }

    @Test
    public void deleteSubtaskEpicInFile() throws IOException, ManagerPrioritizeException, NotFoundException {
        createTestSubtask();
        fileBackedTaskManager.deleteSubtask(subtask.getId());

        String file = fileBackedTaskManager.getTaskManagerCsv().getAbsolutePath();
        String epicString = getFirsNote(file);
        String subtaskString = getSecondNote(file);

        assertAll(
                () -> assertEquals(epic.convertToString(), epicString, "Нет эпика после удаления подзадачи"),
                () -> assertNull(subtaskString, "Подзадача после удаления не удалилась из файла")
        );

    }

    @Test
    public void deleteAllTasksFileIsEmpty() throws IOException, ManagerPrioritizeException {
        createTestTask();
        Task task1 = new Task("Название", "Описание", Duration.ofMinutes(30),
                LocalDateTime.of(2024, 11, 17, 11, 20));
        fileBackedTaskManager.createTask(task1);
        fileBackedTaskManager.deleteAllTasks();

        String file = fileBackedTaskManager.getTaskManagerCsv().getAbsolutePath();
        String nullString = getFirsNote(file);

        assertNull(nullString, "В файле есть какие-то задачи после удаления всех задач");
    }

    @Test
    public void deleteAllEpicsFileIsEmpty() throws IOException {
        createTestEpic();
        createTestEpic();
        fileBackedTaskManager.deleteAllEpics();

        String file = fileBackedTaskManager.getTaskManagerCsv().getAbsolutePath();
        String nullString = getFirsNote(file);

        assertNull(nullString, "В файле есть какие-то задачи после удаления всех эпиков");
    }

    @Test
    public void deleteAllSubtasksEpicInFile() throws IOException, ManagerPrioritizeException, NotFoundException {
        createTestSubtask();
        Subtask subtask1 = new Subtask("Подзадача", "Описание подзадачи", TASKS_DURATION,
                LocalDateTime.of(2024, 11, 17, 11, 20));
        fileBackedTaskManager.createSubtask(subtask1, epic.getId());
        fileBackedTaskManager.deleteAllSubtasks();

        String file = fileBackedTaskManager.getTaskManagerCsv().getAbsolutePath();
        String epicString = getFirsNote(file);
        String subtaskString = getSecondNote(file);
        String otherSubtaskString = getThirdNote(file);

        assertAll(
                () -> assertEquals(epic.convertToString(), epicString, "Эпик удалилс после " +
                        "удаления всех подзадач"),
                () -> assertNull(subtaskString, "Подзадача не удалилась из файла после удаления всех подзадач"),
                () -> assertNull(otherSubtaskString, "Вторая подзадача не удалилась из файла после удаления " +
                        "всех подзадач")
        );

    }

    //вспомогательные методы
    public File createFile() throws IOException {
        if (!Files.exists(Paths.get(resources, "taskManagerCsv.csv"))) {
            Files.createDirectory(Paths.get(resources));
            Files.createFile(Paths.get(resources, "taskManagerCsv.csv"));
        }

        return Paths.get(resources, "taskManagerCsv.csv").toFile();
    }

    public File createTempFile() throws IOException {

        return File.createTempFile("taskManagerCsv", ".csv");
    }

    private void createTestTask() throws ManagerPrioritizeException {
        task = new Task("Задача", "Описание задачи", TASKS_DURATION, TASKS_DATE_TIME);
        fileBackedTaskManager.createTask(task);
    }

    private void createTestEpic() {
        epic = new Epic("Эпик", "Описание эпика");
        fileBackedTaskManager.createEpic(epic);
    }

    private void createTestSubtask() throws ManagerPrioritizeException, NotFoundException {
        epic = new Epic("Эпик", "Описание эпика");
        fileBackedTaskManager.createEpic(epic);
        subtask = new Subtask("Подзадача", "Описание подзадачи", TASKS_DURATION, TASKS_DATE_TIME);
        fileBackedTaskManager.createSubtask(subtask, epic.getId());
        fileBackedTaskManager.updateEpic(epic, epic.getId());
    }

    private static String getFirsNote(String file) throws IOException {
        Reader reader = new FileReader(file, UTF_8);
        BufferedReader bufferedReader = new BufferedReader(reader);
        bufferedReader.readLine(); //пропускаем шапку csv

        return bufferedReader.readLine();
    }

    private static String getSecondNote(String file) throws IOException  {
        Reader reader = new FileReader(file, UTF_8);
        BufferedReader bufferedReader = new BufferedReader(reader);
        bufferedReader.readLine(); //пропускаем шапку csv
        bufferedReader.readLine(); //пропускаем первую запись

        return bufferedReader.readLine();
    }

    private static String getThirdNote(String file) throws IOException {
        Reader reader = new FileReader(file, UTF_8);
        BufferedReader bufferedReader = new BufferedReader(reader);
        bufferedReader.readLine(); //пропускаем шапку csv
        bufferedReader.readLine(); //пропускаем первую запись
        bufferedReader.readLine(); //пропускаем вторую запись

        return bufferedReader.readLine();
    }
}
