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

public class TaskHandlerTest extends BaseHandlerTest {

    Task task;
    Task task1;

    @Test
    public void postTaskWithoutIDReturn200() {
        URI uri = createUri("/tasks");

        HttpRequest request = postRequestWithBody(uri, taskBody());
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        try {
            HttpResponse<String> response = client.send(request, handler);

            assertEquals(201, response.statusCode(), "Код статуса запроса на создание задачи не 201");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void postTaskWithoutIDCreateTask() {
        createTask(taskBody());
        assertEquals(1, taskManager.getTasks().size(), "В Таскменеджере подозрительное количество задач");
    }

    @Test
    public void getTasksReturn200() {
        createTask(taskBody());
        URI uriWithId = createUri("/tasks/1");

        HttpRequest request = getRequest(uriWithId);
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        try {
            HttpResponse<String> response = client.send(request, handler);

            assertEquals(200, response.statusCode(), "Код статуса запроса на создание задачи не 201");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getTaskReturnTask() {
        createTask(taskBody());
        URI uriWithId = createUri("/tasks/1");

        HttpRequest request = getRequest(uriWithId);
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        try {
            HttpResponse<String> response = client.send(request, handler);

            if (response.statusCode() == 200) {
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

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getTasksReturnAllTasksAnd200() {
        createTask(taskBody());
        createTask(anotherTaskBody());

        URI uri = createUri("/tasks");
        HttpRequest request = getRequest(uri);
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        try {
            HttpResponse<String> response = client.send(request, handler);

            if (response.statusCode() == 200) {
                JsonElement jsonElement = JsonParser.parseString(response.body());

                assertAll(
                        () -> assertEquals(200, response.statusCode(), "Код статуса на запрос всего" +
                                "списка задач не 200"),
                        () -> assertTrue(jsonElement.isJsonArray(), "Вернули не массив задач")
                );
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


    }

    @Test
    public void postTaskWithIdUpdateTaskAnd201() {
        createTask(taskBody());
        URI uriWithId = createUri("/tasks/1");
        HttpRequest request = postRequestWithBody(uriWithId, anotherTaskBody());
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        try {
            HttpResponse<String> response = client.send(request, handler);

            assertAll(
                    () -> assertEquals(201, response.statusCode(), "Код апдейта не 201"),
                    () -> assertEquals(task1.getStatus(), taskManager.getTask(1).getStatus(), "Задача " +
                            "в таскменеджере не изменилась")
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void deleteTaskWithoutIdTasksDeletedAnd201() {
        createTask(taskBody());
        URI uriWithoutId = createUri("/tasks");
        HttpRequest request = deleteRequest(uriWithoutId);
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        try {
            HttpResponse<String> response = client.send(request, handler);

            assertAll(
                    () -> assertEquals(201, response.statusCode(), "При удалении всех задач статус не 201"),
                    () -> assertTrue(taskManager.getAllTasks().isEmpty(), "Задача не удалилась")
            );

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void deleteTaskWithIdTaskDeletedAnd201() {
        createTask(taskBody());
        createTask(anotherTaskBody());
        URI uriWithId = createUri("/tasks/1");
        HttpRequest request = deleteRequest(uriWithId);
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        try {
            HttpResponse<String> response = client.send(request, handler);

            assertAll(
                    () -> assertEquals(201, response.statusCode(), "При удалении задачи код не 201"),
                    () -> assertFalse(taskManager.getTasks().containsKey(1))
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    //вспомогательные методы
    public String taskBody() {
        task = new Task("Задача", "Описание задачи", Duration.ofMinutes(20),
                LocalDateTime.of(2024, 11, 17, 11, 20));

        return gson.toJson(task);
    }

    public String anotherTaskBody() {
        task1 = new Task("Другая задача", "Другое описание задачи", Status.IN_PROGRESS, Duration.ofMinutes(30),
                LocalDateTime.of(2024, 11, 15, 14, 00));

        return gson.toJson(task1);
    }

    public void createTask(String body) {
        URI uri = createUri("/tasks");

        HttpRequest request = postRequestWithBody(uri, body);
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        try {
            client.send(request, handler);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
