package ru.practicum.javakanban.http;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import ru.practicum.javakanban.exeptions.NotFoundException;
import ru.practicum.javakanban.model.Epic;
import ru.practicum.javakanban.model.Subtask;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.javakanban.http.Constans.GSON;

public class SubtaskHandleTest extends BaseHandlerTest {
    Epic epic;
    Subtask subtask;
    Subtask anotherSubtask;

    @Test
    public void postSubtaskWithoutIdCreateSubtaskAnd201() throws IOException, InterruptedException {
        createEpicId1();
        URI uriWithoutId = createUri("/subtasks");
        HttpRequest request = postRequestWithBody(uriWithoutId, subtaskBody());
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        HttpResponse<String> response = client.send(request, handler);
        assertAll(
                () -> assertEquals(201, response.statusCode(), "Код при создании сабтаски не 201"),
                () -> assertFalse(taskManager.getSubtasks().isEmpty(), "Сабтаска не создалась")
        );
    }

    @Test
    public void postSubtaskWithIdUpdateSubtaskAnd201() throws IOException, InterruptedException {
        createEpicAndSubtask();
        URI uriWithId = createUri("/subtasks/2");
        HttpRequest request = postRequestWithBody(uriWithId, anotherSubtaskBody());
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        HttpResponse<String> response = client.send(request, handler);
        assertAll(
                () -> assertEquals(201, response.statusCode(), "Код при апдейте сабтаски не 201"),
                () -> assertEquals(anotherSubtask.getName(), taskManager.getSubtask(2).getName())
        );
    }

    @Test
    public void getSubtaskWithoutIdReturn200AndArray() throws IOException, InterruptedException {
        createEpicAndTwoSubtasks();
        URI uriWithoutId = createUri("/subtasks");
        HttpRequest request = getRequest(uriWithoutId);
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        HttpResponse<String> response = client.send(request, handler);
        JsonElement jsonElement = JsonParser.parseString(response.body());

        assertAll(
                () -> assertEquals(200, response.statusCode(), "При запрове всех сабтасок код не 200"),
                () -> assertTrue(jsonElement.isJsonArray(), "Вернули не список при запросе всех тасок")
        );
    }

    @Test
    public void getSubtaskWithIdReturn200() throws IOException, InterruptedException {
        createEpicAndSubtask();
        URI uriWithId = createUri("/subtasks/2");
        HttpRequest request = getRequest(uriWithId);
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        HttpResponse<String> response = client.send(request, handler);

        assertEquals(200, response.statusCode(), "При запросе сабтаски код не 200");

    }

    @Test
    public void getSubtaskWithIdReturnSubtask() throws IOException, InterruptedException, NotFoundException {
        createEpicAndSubtask();
        URI uriWithId = createUri("/subtasks/2");
        HttpRequest request = getRequest(uriWithId);
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        HttpResponse<String> response = client.send(request, handler);
        JsonElement jsonElement = JsonParser.parseString(response.body());

        if (jsonElement.isJsonObject()) {
            JsonObject object = jsonElement.getAsJsonObject();

            assertEquals(taskManager.getSubtask(2).getName(), object.get("name").getAsString());
        } else {
            System.out.println("Что-то подозрительное вернулось при запросе сабтаски");
        }

    }

    @Test
    public void deleteSubtaskWithoutIdReturn201AndDeleteAllSubtasks() throws IOException, InterruptedException {
        createEpicAndTwoSubtasks();
        URI uriWithoutId = createUri("/subtasks");
        HttpRequest request = deleteRequest(uriWithoutId);
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        HttpResponse<String> response = client.send(request, handler);
        assertAll(
                () -> assertEquals(201, response.statusCode(), "При удалении всех сабтасок код не 201"),
                () -> assertTrue(taskManager.getSubtasks().isEmpty(), "Сабтаски не удалились")
        );
    }

    @Test
    public void deleteSubtaskWithIdReturn201AndDeletedSubtask() throws IOException, InterruptedException {
        createEpicAndTwoSubtasks();
        URI uriWithId = createUri("/subtasks/3");
        HttpRequest request = deleteRequest(uriWithId);
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        HttpResponse<String> response = client.send(request, handler);

        assertAll(
                () -> assertEquals(201, response.statusCode(), "При удалении сабтаски код не 201"),
                () -> assertFalse(taskManager.getSubtasks().containsKey(3)),
                () -> assertFalse(taskManager.getSubtasks().isEmpty())
        );
    }

    @Test
    public void sendInvalidMethodReturn404() throws IOException, InterruptedException {
        createEpicId1();
        URI uri = createUri("/epics");
        HttpRequest request = putInvalidMethod(uri, (subtaskBody()));
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        HttpResponse<String> response = client.send(request, handler);
        assertEquals(404, response.statusCode(), "Код не 404 при невалидном методе");
    }

    @Test
    public void getSubtaskWithIncorrectIdReturn404() throws IOException, InterruptedException {
        URI uri = createUri("/subtasks/" + INCORRECT_ID);
        HttpRequest request = getRequest(uri);
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        HttpResponse<String> response = client.send(request, handler);
        assertEquals(404, response.statusCode(), "Код не 404 при запросе несуществующей сабтаски");

    }

    @Test
    public void postSubtaskWithIncorrectIdReturn404() throws IOException, InterruptedException {
        createEpicAndSubtask();
        URI uriWithId = createUri("/subtasks/" + INCORRECT_ID);
        HttpRequest request = postRequestWithBody(uriWithId, anotherSubtaskBody());
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        HttpResponse<String> response = client.send(request, handler);
        assertEquals(404, response.statusCode(), "Не 404 при апдете несуществующей сабтаски");
    }

    @Test
    public void deleteSubtaskWithIncorrectIdReturn404() throws IOException, InterruptedException {
        createEpicAndSubtask();
        URI uriWithId = createUri("/subtasks/" + INCORRECT_ID);
        HttpRequest request = deleteRequest(uriWithId);
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        HttpResponse<String> response = client.send(request, handler);
        assertEquals(404, response.statusCode(), "Не 404 при удалении несуществующей сабтаски");
    }

    //вспомогательные методы

    public void createEpicId1() {
        epic = new Epic("Эпик", "Описание эпика");
        taskManager.createEpic(epic);
    }

    public String subtaskBody() {
        subtask = new Subtask("Название подзадачи", "Описание подзадачи", 1,
                Duration.ofMinutes(15), LocalDateTime.of(2024, 10, 30, 12, 10));
        return GSON.toJson(subtask);
    }

    public String anotherSubtaskBody() {
        anotherSubtask = new Subtask("Другое название подзадачи", "Другое описание подзадачи", 1,
                Duration.ofMinutes(30), LocalDateTime.of(2024, 11, 2, 15, 15));
        return GSON.toJson(anotherSubtask);
    }

    public void createEpicAndSubtask() {
        createEpicId1();
        URI uriWithoutId = createUri("/subtasks");
        HttpRequest request = postRequestWithBody(uriWithoutId, subtaskBody());
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        try {
            client.send(request, handler);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void createEpicAndTwoSubtasks() {
        createEpicAndSubtask();
        URI uriWithoutId = createUri("/subtasks");
        HttpRequest request = postRequestWithBody(uriWithoutId, anotherSubtaskBody());
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        try {
            client.send(request, handler);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
