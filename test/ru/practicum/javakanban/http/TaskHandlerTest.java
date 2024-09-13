package ru.practicum.javakanban.http;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import ru.practicum.javakanban.model.Status;
import ru.practicum.javakanban.model.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.javakanban.http.Constans.GSON;

public class TaskHandlerTest extends BaseHandlerTest {

    Task task;
    Task task1;

    @Test
    public void postTaskWithoutIDReturn200() throws IOException, InterruptedException {
        URI uri = createUri("/tasks");

        HttpRequest request = postRequestWithBody(uri, taskBody());
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        HttpResponse<String> response = client.send(request, handler);

        assertEquals(201, response.statusCode(), "Код статуса запроса на создание задачи не 201");
    }

    @Test
    public void postTaskWithoutIDCreateTask() {
        createTask(taskBody());
        assertEquals(1, taskManager.getTasks().size(), "В Таскменеджере подозрительное количество задач");
    }

    @Test
    public void getTasksWithIdReturn200() throws IOException, InterruptedException {
        createTask(taskBody());
        URI uriWithId = createUri("/tasks/1");

        HttpRequest request = getRequest(uriWithId);
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        HttpResponse<String> response = client.send(request, handler);

        assertEquals(200, response.statusCode(), "Код статуса запроса на создание задачи не 201");
    }

    @Test
    public void getTaskReturnTask() throws IOException, InterruptedException {
        createTask(taskBody());
        URI uriWithId = createUri("/tasks/1");

        HttpRequest request = getRequest(uriWithId);
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        HttpResponse<String> response = client.send(request, handler);

        JsonElement jsonElement = JsonParser.parseString(response.body());

        if (jsonElement.isJsonObject()) {
            JsonObject object = jsonElement.getAsJsonObject();
            assertAll(
                    () -> assertEquals(1, object.get("id").getAsInt()),
                    () -> assertEquals(task.getName(), object.get("name").getAsString()),
                    () -> assertEquals(task.getDescription(), object.get("description").getAsString()),
                    () -> assertEquals("NEW", object.get("status").getAsString()),
                    () -> assertEquals(20, object.get("duration").getAsInt()),
                    () -> assertEquals("2024-11-17, 11:20", object.get("startTime").getAsString())
            );

        } else {
            System.out.println("Тело ответа не соответствует ожиданиям");
        }
    }

    @Test
    public void getTasksReturnAllTasksAnd200() throws IOException, InterruptedException {
        createTask(taskBody());
        createTask(anotherTaskBody());

        URI uri = createUri("/tasks");
        HttpRequest request = getRequest(uri);
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        HttpResponse<String> response = client.send(request, handler);
        JsonElement jsonElement = JsonParser.parseString(response.body());

        assertAll(
                () -> assertEquals(200, response.statusCode(), "Код статуса на запрос " +
                        "всего списка задач не 200"),
                () -> assertTrue(jsonElement.isJsonArray(), "Вернули не массив задач")
        );

    }

    @Test
    public void postTaskWithIdUpdateTaskAnd201() throws IOException, InterruptedException {
        createTask(taskBody());
        URI uriWithId = createUri("/tasks/1");
        HttpRequest request = postRequestWithBody(uriWithId, anotherTaskBody());
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        HttpResponse<String> response = client.send(request, handler);

        assertAll(
                () -> assertEquals(201, response.statusCode(), "Код апдейта не 201"),
                () -> assertEquals(task1.getStatus(), taskManager.getTask(1).getStatus(), "Задача " +
                        "в таскменеджере не изменилась")
        );
    }

    @Test
    public void deleteTaskWithoutIdTasksDeletedAnd201() throws IOException, InterruptedException {
        createTask(taskBody());
        URI uriWithoutId = createUri("/tasks");
        HttpRequest request = deleteRequest(uriWithoutId);
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        HttpResponse<String> response = client.send(request, handler);

        assertAll(
                () -> assertEquals(201, response.statusCode(), "При удалении всех задач статус не 201"),
                () -> assertTrue(taskManager.getAllTasks().isEmpty(), "Задача не удалилась")
        );
    }

    @Test
    public void deleteTaskWithIdTaskDeletedAnd201() throws IOException, InterruptedException {
        createTask(taskBody());
        createTask(anotherTaskBody());
        URI uriWithId = createUri("/tasks/1");
        HttpRequest request = deleteRequest(uriWithId);
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        HttpResponse<String> response = client.send(request, handler);

        assertAll(
                () -> assertEquals(201, response.statusCode(), "При удалении задачи код не 201"),
                () -> assertFalse(taskManager.getTasks().containsKey(1))
        );
    }

    @Test
    public void sendInvalidMethodReturn404() throws IOException, InterruptedException {
        URI uri = createUri("/task");
        HttpRequest request = putInvalidMethod(uri, taskBody());
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        HttpResponse<String> response = client.send(request, handler);
        assertEquals(404, response.statusCode(), "Код не 404 при невалидном методе");
    }

    @Test
    public void getTaskWithIncorrectIdReturn404() throws IOException, InterruptedException {
        createTask(taskBody());
        URI uriWithId = createUri("/tasks/" + INCORRECT_ID);

        HttpRequest request = getRequest(uriWithId);
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        HttpResponse<String> response = client.send(request, handler);

        assertEquals(404, response.statusCode(), "Не 404 при запросе несуществующей задачи");
    }

    @Test
    public void postTaskWithIncorrectIdReturn404() throws IOException, InterruptedException {
        createTask(taskBody());
        URI uriWithId = createUri("/tasks/" + INCORRECT_ID);
        HttpRequest request = postRequestWithBody(uriWithId, anotherTaskBody());
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        HttpResponse<String> response = client.send(request, handler);
        assertEquals(404, response.statusCode(), "Не 404 при попытке апдейта несуществующей таски");

    }

    @Test
    public void deleteTaskWithIncorrectId() throws IOException, InterruptedException {
        createTask(taskBody());
        createTask(anotherTaskBody());
        URI uriWithId = createUri("/tasks/" + INCORRECT_ID);
        HttpRequest request = deleteRequest(uriWithId);
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        HttpResponse<String> response = client.send(request, handler);
        assertEquals(404, response.statusCode(), "Не 404 при попытке удаления несуществующей таски");
    }

    //вспомогательные методы
    public String taskBody() {
        task = new Task("Задача", "Описание задачи", Duration.ofMinutes(20),
                LocalDateTime.of(2024, 11, 17, 11, 20));

        return GSON.toJson(task);
    }

    public String anotherTaskBody() {
        task1 = new Task("Другая задача", "Другое описание задачи", Status.IN_PROGRESS, Duration.ofMinutes(30),
                LocalDateTime.of(2024, 11, 15, 14, 0));

        return GSON.toJson(task1);
    }

    public void createTask(String body) {
        URI uri = createUri("/tasks");

        HttpRequest request = postRequestWithBody(uri, body);
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        try {
            client.send(request, handler);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
