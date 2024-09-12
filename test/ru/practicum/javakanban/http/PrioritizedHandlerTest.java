package ru.practicum.javakanban.http;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import ru.practicum.javakanban.exeptions.ManagerPrioritizeException;
import ru.practicum.javakanban.model.Epic;
import ru.practicum.javakanban.model.Subtask;
import ru.practicum.javakanban.model.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class PrioritizedHandlerTest extends BaseHandlerTest {

    @Test
    public void getPrioritizedListReturn200() throws ManagerPrioritizeException {
        createTasks();
        URI uri = createUri("/prioritized");
        HttpRequest request = getRequest(uri);
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        try {
            HttpResponse<String> response = client.send(request, handler);
            JsonElement jsonElement = JsonParser.parseString(response.body());

            assertAll(
                    () -> assertEquals(200, response.statusCode(), "При запросы списка приоритета код не 200"),
                    () -> assertTrue(jsonElement.isJsonArray(), "Прислали что-то не то при запросе списка приоритета")
            );

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void sendInvalidMethodReturn404() throws ManagerPrioritizeException {
        createTasks();
        URI uri = createUri("/prioritized");
        HttpRequest request = deleteRequest(uri);
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        try {
            HttpResponse<String> response = client.send(request, handler);
            assertEquals(404, response.statusCode(), "Код не 404 при невалидном методе");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    //вспомогательные методы
    public void createTasks() throws ManagerPrioritizeException {
        Task task1 = new Task("Задача № 1", "Описание задачи № 1", Duration.ofMinutes(30),
                LocalDateTime.of(2024, 10, 15, 12, 30));
        taskManager.createTask(task1);

        Epic epic = new Epic("Эпик", "Описание эпика");
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask("Подзадача", "Описание подзадачи", Duration.ofMinutes(30),
                LocalDateTime.of(2024, 11, 5, 15, 0));
        taskManager.createSubtask(subtask, epic.getId());

        Task task2 = new Task("Задача № 2", "Описание задачи № 2", Duration.ofMinutes(30),
                LocalDateTime.of(2024, 9, 7, 10, 15));
        taskManager.createTask(task2);
    }
}
