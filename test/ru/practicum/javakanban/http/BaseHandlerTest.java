package ru.practicum.javakanban.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import ru.practicum.javakanban.manager.Managers;
import ru.practicum.javakanban.manager.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public abstract class BaseHandlerTest {

    protected final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    protected final String baseURL = "http://localhost:8080";
    HttpTaskServer taskServer;
    HttpClient client = HttpClient.newHttpClient();
    TaskManager taskManager = Managers.getDefault();
    String INCORRECT_ID = "120";

    Gson gson = new GsonBuilder()
            .serializeNulls()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();


    @BeforeEach
    public void createTaskServer() {
        try {
            taskServer = new HttpTaskServer(taskManager);
            taskServer.createHandlers();
            taskServer.startServer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    public void stopTaskServer() {
        taskServer.stopServer();
    }

    //вспомогательные методы

    public URI createUri(String string) {
        String uri = baseURL + string;
        return URI.create(uri);
    }

    public HttpRequest postRequestWithBody(URI uri, String body) {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        return requestBuilder
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(body, DEFAULT_CHARSET))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json;charset=utf-8")
                .build();
    }

    public HttpRequest getRequest(URI uri) {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        return requestBuilder
                .uri(uri)
                .GET()
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json;charset=utf-8")
                .build();
    }

    public HttpRequest deleteRequest(URI uri) {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        return requestBuilder
                .uri(uri)
                .DELETE()
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json;charset=utf-8")
                .build();
    }

    public HttpRequest putInvalidMethod(URI uri, String body) {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        return requestBuilder
                .uri(uri)
                .PUT(HttpRequest.BodyPublishers.ofString(body, DEFAULT_CHARSET))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json;charset=utf-8")
                .build();
    }
}
