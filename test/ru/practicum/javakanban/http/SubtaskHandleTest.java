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

public class SubtaskHandleTest extends BaseHandlerTest {
    Epic epic;
    Subtask subtask;
    Subtask anotherSubtask;

    @Test
    public void postSubtaskWithoutIdCreateSubtaskAnd201() {
        createEpicId1();
        URI uriWithoutId = createUri("/subtasks");
        HttpRequest request = postRequestWithBody(uriWithoutId, subtaskBody());
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        try {
            HttpResponse<String> response = client.send(request, handler);
            assertAll(
                    () -> assertEquals(201, response.statusCode(), "Код при создании сабтаски не 201"),
                    () -> assertTrue(!(taskManager.getSubtasks().isEmpty()), "Сабтаска не создалась")
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void postSubtaskWithIdUpdateSubtaskAnd201() {
        createEpicAndSubtask();
        URI uriWithId = createUri("/subtasks/2");
        HttpRequest request = postRequestWithBody(uriWithId, anotherSubtaskBody());
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        try {
            HttpResponse<String> response = client.send(request, handler);
            assertAll(
                    () -> assertEquals(201, response.statusCode(), "Код при апдейте сабтаски не 201"),
                    () -> assertEquals(anotherSubtask.getName(), taskManager.getSubtask(2).getName())
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getSubtaskWithoutIdReturn200() {
        createEpicAndTwoSubtasks();
        URI uriWithoutId = createUri("/subtasks");
        HttpRequest request = getRequest(uriWithoutId);
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        try {
            HttpResponse<String> response = client.send(request, handler);

            assertEquals(200, response.statusCode(), "При запрове всех сабтасок код не 200");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getSubtaskReturnArray() {
        createEpicAndTwoSubtasks();
        URI uriWithoutId = createUri("/subtasks");
        HttpRequest request = getRequest(uriWithoutId);
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        try {
            HttpResponse<String> response = client.send(request, handler);
            if (response.statusCode() == 200) {
                JsonElement jsonElement = JsonParser.parseString(response.body());

                assertTrue(jsonElement.isJsonArray());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getSubtaskWithIdReturn200() {
        createEpicAndSubtask();
        URI uriWithId = createUri("/subtasks/2");
        HttpRequest request = getRequest(uriWithId);
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        try {
            HttpResponse<String> response = client.send(request, handler);

            assertEquals(200, response.statusCode(), "При запросе сабтаски код не 200");

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getSubtaskWithIdReturnSubtask() {
        createEpicAndSubtask();
        URI uriWithId = createUri("/subtasks/2");
        HttpRequest request = getRequest(uriWithId);
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        try {
            HttpResponse<String> response = client.send(request, handler);

            if (response.statusCode() == 200) {
               JsonElement jsonElement = JsonParser.parseString(response.body());

               if (jsonElement.isJsonObject()) {
                   JsonObject object = jsonElement.getAsJsonObject();

                   assertEquals(taskManager.getSubtask(2).getName(), object.get("name").getAsString());
               }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void deleteSubtaskWithoutIdReturn201AndDeleteAllSubtasks() {
        createEpicAndTwoSubtasks();
        URI uriWithoutId = createUri("/subtasks");
        HttpRequest request = deleteRequest(uriWithoutId);
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        try {
            HttpResponse<String> response = client.send(request, handler);
            assertAll(
                    () -> assertEquals(201, response.statusCode(), "При удалении всех сабтасок код не 201"),
                    () -> assertTrue(taskManager.getSubtasks().isEmpty(), "Сабтаски не удалились")
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void deleteSubtaskWithIdReturn201AndDeletedSubtask() {
        createEpicAndTwoSubtasks();
        URI uriWithId = createUri("/subtasks/3");
        HttpRequest request = deleteRequest(uriWithId);
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        try {
            HttpResponse<String> response = client.send(request, handler);

            assertAll(
                    () -> assertEquals(201, response.statusCode(), "При удалении сабтаски код не 201"),
                    () -> assertFalse(taskManager.getSubtasks().containsKey(3)),
                    () -> assertFalse(taskManager.getSubtasks().isEmpty())
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void sendInvalidMethodReturn404() {
        createEpicId1();
        URI uri = createUri("/epics");
        HttpRequest request = putInvalidMethod(uri, (subtaskBody()));
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

    @Test
    public void getSubtaskWithIncorrectIdReturn404() {
        URI uri = createUri("/subtasks/" + INCORRECT_ID);
        HttpRequest request = getRequest(uri);
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        try {
            HttpResponse<String> response = client.send(request, handler);
            assertEquals(404, response.statusCode(), "Код не 404 при запросе несуществующей сабтаски");

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void postSubtaskWithIncorrectIdReturn404() {
        createEpicAndSubtask();
        URI uriWithId = createUri("/subtasks/" + INCORRECT_ID);
        HttpRequest request = postRequestWithBody(uriWithId, anotherSubtaskBody());
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        try {
            HttpResponse<String> response = client.send(request, handler);
            assertEquals(404, response.statusCode(), "Не 404 при апдете несуществующей сабтаски");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void deleteSubtaskWithIncorrectIdReturn404() {
        createEpicAndSubtask();
        URI uriWithId = createUri("/subtasks/" + INCORRECT_ID);
        HttpRequest request = deleteRequest(uriWithId);
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        try {
            HttpResponse<String> response = client.send(request, handler);
            assertEquals(404, response.statusCode(), "Не 404 при удалении несуществующей сабтаски");

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    //вспомогательные методы

    public void createEpicId1() {
        epic = new Epic("Эпик", "Описание эпика");
        taskManager.createEpic(epic);
    }

    public String subtaskBody() {
        subtask = new Subtask("Название подзадачи", "Описание подзадачи", 1,
                Duration.ofMinutes(15), LocalDateTime.of(2024, 10, 30, 12, 10));
        return gson.toJson(subtask);
    }

    public String anotherSubtaskBody() {
        anotherSubtask = new Subtask("Другое название подзадачи", "Другое описание подзадачи", 1,
                Duration.ofMinutes(30), LocalDateTime.of(2024, 11, 2, 15, 15));
        return gson.toJson(anotherSubtask);
    }

    public void createEpicAndSubtask() {
        createEpicId1();
        URI uriWithoutId = createUri("/subtasks");
        HttpRequest request = postRequestWithBody(uriWithoutId, subtaskBody());
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        try {
            HttpResponse<String> response = client.send(request, handler);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void createEpicAndTwoSubtasks() {
        createEpicAndSubtask();
        URI uriWithoutId = createUri("/subtasks");
        HttpRequest request = postRequestWithBody(uriWithoutId, anotherSubtaskBody());
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        try {
            HttpResponse<String> response = client.send(request, handler);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
