package ru.practicum.javakanban.http;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import ru.practicum.javakanban.exeptions.ManagerPrioritizeException;
import ru.practicum.javakanban.model.Epic;
import ru.practicum.javakanban.model.Subtask;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

import static ru.practicum.javakanban.http.Constans.*;

public class EpicHandleTest extends BaseHandlerTest {
    Epic epic;
    Epic anotherEpic;

    @Test
    public void postEpicWithoutEpicCreateAnd201() {
        URI uri = createUri("/epics");
        HttpRequest request = postRequestWithBody(uri, epicBody());
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        try {
            HttpResponse<String> response = client.send(request, handler);

            assertAll(
                    () -> assertEquals(201, response.statusCode(), "Статус при создании эпика не 201"),
                    () -> assertEquals(1, taskManager.getEpics().size(), "Странное количество эпиков" +
                            " в таскменеджере")
            );

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void postEpicWithIdEpicUpdatedAnd201() {
        createEpic(epicBody());
        URI uriWithId = createUri("/epics/1");
        HttpRequest request = postRequestWithBody(uriWithId, anotherEpicBody());
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        try {
            HttpResponse<String> response = client.send(request, handler);

            assertAll(
                    () -> assertEquals(201, response.statusCode(), "Код при апдейте эпика не 201"),
                    () -> assertEquals(anotherEpic.getName(), taskManager.getEpic(1).getName())
            );

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getEpicWithIdReturn200() {
        createEpic(epicBody());
        URI uriWithId = createUri("/epics/1");
        HttpRequest request = getRequest(uriWithId);
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        try {
            HttpResponse<String> response = client.send(request, handler);

            assertEquals(200, response.statusCode(), "При запросе эпика код не 200");

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getEpicWithIdReturnEpic() {
        createEpic(epicBody());
        URI uriWithId = createUri("/epics/1");
        HttpRequest request = getRequest(uriWithId);
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        try {
            HttpResponse<String> response = client.send(request, handler);

            JsonElement jsonElement = JsonParser.parseString(response.body());

            if (jsonElement.isJsonObject()) {
                JsonObject object = jsonElement.getAsJsonObject();

                assertAll(
                        () -> assertEquals(epic.getName(), object.get("name").getAsString()),
                        () -> assertEquals(epic.getDescription(), object.get("description").getAsString())
                );
            } else {
                System.out.println("Тело ответа не соответствует ожиданиям");
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    public void getAllEpicsReturn200AndArray() {
        createEpic(epicBody());
        createEpic(anotherEpicBody());
        URI urlWithoutId = createUri("/epics");
        HttpRequest request = getRequest(urlWithoutId);
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        try {
            HttpResponse<String> response = client.send(request, handler);
            JsonElement jsonElement = JsonParser.parseString(response.body());

            assertAll(
                    () -> assertEquals(200, response.statusCode(), "При запросе всех эпиков код не 200"),
                    () -> assertTrue(jsonElement.isJsonArray(), "Вернули не массив эпиков")
            );

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getEpicSubtaskReturn200AndArray() throws ManagerPrioritizeException {
        createEpicAndSubtasks();
        URI uriWithId = createUri("/epics/1/subtasks");
        HttpRequest request = getRequest(uriWithId);
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        try {
            HttpResponse<String> response = client.send(request, handler);
            JsonElement jsonElement = JsonParser.parseString(response.body());

            assertAll(
                    () -> assertEquals(200, response.statusCode(), "Код не 200 при запросе сабтасок"),
                    () -> assertTrue(jsonElement.isJsonArray())
            );
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void deleteEpicWithIdReturn201AndEpicDeleted() {
        createEpic(epicBody());
        createEpic(anotherEpicBody());
        URI uriWithId = createUri("/epics/1");
        HttpRequest request = deleteRequest(uriWithId);
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        try {
            HttpResponse<String> response = client.send(request, handler);

            assertAll(
                    () -> assertEquals(201, response.statusCode(), "При удалении эпика кож не 201"),
                    () -> assertFalse(taskManager.getEpics().containsKey(1), "Эпик не удалился")
            );
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void deleteEpicWithoutIdDeletedAllEpicsAnd201() {
        createEpic(epicBody());
        createEpic(anotherEpicBody());
        URI uriWithoutId = createUri("/epics");
        HttpRequest request = deleteRequest(uriWithoutId);
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        try {
            HttpResponse<String> response = client.send(request, handler);

            assertAll(
                    () -> assertEquals(201, response.statusCode(), "При удалении эпика кож не 201"),
                    () -> assertTrue(taskManager.getEpics().isEmpty(), "Эпики не удалился")
            );
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void sendInvalidMethodReturn404() {
        URI uri = createUri("/epics");
        HttpRequest request = putInvalidMethod(uri, epicBody());
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        try {
            HttpResponse<String> response = client.send(request, handler);
            assertEquals(404, response.statusCode(), "Код не 404 при невалидном методе");
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getEpicIncorrectIdReturn404() {
        createEpic(epicBody());
        URI uri = createUri("/epics/" + INCORRECT_ID);
        HttpRequest request = getRequest(uri);
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        try {
            HttpResponse<String> response = client.send(request, handler);
            assertEquals(404, response.statusCode(), "Код не 404 при запросе несуществующего эпика");

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getEpicSubtaskIncorrectIdReturn404() {
        createEpic(epicBody());
        URI uri = createUri("/epics/" + INCORRECT_ID + "/subtasks");
        HttpRequest request = getRequest(uri);
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        try {
            HttpResponse<String> response = client.send(request, handler);
            assertEquals(404, response.statusCode(), "Код не 404 при запросе сабтасок несуществующего " +
                    "эпика");

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getIncorrectPathForGetEpicSubtasksReturn404() throws ManagerPrioritizeException {
        createEpicAndSubtasks();
        URI uri = createUri("/epics/1/subtask");
        HttpRequest request = getRequest(uri);
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        try {
            HttpResponse<String> response = client.send(request, handler);
            assertEquals(404, response.statusCode(), "Код не 404 при неправильном пути до сабтасок");

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void deleteIncorrectIdReturn404() {
        createEpic(epicBody());
        URI uri = createUri("/epics/" + INCORRECT_ID);
        HttpRequest request = deleteRequest(uri);
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        try {
            HttpResponse<String> response = client.send(request, handler);
            assertEquals(404, response.statusCode(), "Код не 404 удалении несуществующего эпика");

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    // вспомогательные методы

    public String epicBody() {
        epic = new Epic("Эпик", "Описание");
        return GSON.toJson(epic);
    }

    public String anotherEpicBody() {
        anotherEpic = new Epic("Другой эпик", "Описание тоже другое");
        return GSON.toJson(anotherEpic);
    }

    public void createEpic(String body) {
        URI uri = createUri("/epics");
        HttpRequest request = postRequestWithBody(uri, body);
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        try {
            client.send(request, handler);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void createEpicAndSubtasks() throws ManagerPrioritizeException {
        createEpic(epicBody());
        Subtask subtask = new Subtask("Подзадача", "Описание подзадачи", Duration.ofMinutes(60),
                LocalDateTime.of(2024, 11, 5, 15, 20));
        taskManager.createSubtask(subtask, 1);
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", Duration.ofMinutes(60),
                LocalDateTime.of(2024, 11, 10, 16, 30));
        taskManager.createSubtask(subtask, 1);
    }

}
