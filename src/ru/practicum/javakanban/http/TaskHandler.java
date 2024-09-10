package ru.practicum.javakanban.http;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.practicum.javakanban.exeptions.ManagerPrioritizeException;
import ru.practicum.javakanban.exeptions.NotFoundException;
import ru.practicum.javakanban.manager.TaskManager;
import ru.practicum.javakanban.model.Task;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    TaskManager taskManager;
    Gson gson;


    public TaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        gson = new GsonBuilder()
                .serializeNulls()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    @Override
    public void handle(HttpExchange exchange) {
        String method = exchange.getRequestMethod();

        switch (method) {
            case "GET":
                handleGet(exchange);
                break;
            case "POST":
                handlePost(exchange);
                break;
            case "DELETE":

                break;
            default:

                break;
        }
    }

    public void handleGet(HttpExchange exchange) {
        String[] params = getParams(exchange);

        switch (params.length) {
            case 2:
                String tasks = gson.toJson(taskManager.getAllTasks());
                sendText(exchange, tasks);
                break;
            case 3:
                Optional<Integer> optId = getId(exchange);

                if (optId.isEmpty()) {
                    sendBadRequest(exchange, "Невереный id задачи");
                } else {
                    try {
                        String task = gson.toJson(taskManager.getTask(optId.get()));
                        sendText(exchange, task);
                    } catch (NotFoundException e) {
                        sendBadRequest(exchange, e.getMessage());
                    }
                }
                break;
            default:
                sendNotFound(exchange);
                break;
        }

    }

    public void handlePost(HttpExchange exchange) {
        String[] params = getParams(exchange);

        switch (params.length) {
            case 2:
                try {
                    Task task = parseTask(exchange);
                    taskManager.createTask(task);
                    sendCreated(exchange);
                } catch (IOException e) {
                    System.out.println("Во время получения запроса возникла ошибка.");
                } catch (ManagerPrioritizeException e) {
                    sendHasInteractions(exchange);
                } catch (IllegalArgumentException e) {
                    sendBadRequest(exchange, e.getMessage());
                }
                break;
            case 3:
                Optional<Integer> optId = getId(exchange);

                if (optId.isEmpty()) {
                    sendBadRequest(exchange, "Невереный id задачи");
                } else {
                    try {
                        Task newTask = parseTask(exchange);
                        taskManager.updateTask(newTask, optId.get());
                    } catch (IllegalArgumentException e) {
                        sendBadRequest(exchange, e.getMessage());
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    } catch (ManagerPrioritizeException e) {
                        sendHasInteractions(exchange);
                    }
                }
                break;
        }
    }

    public Task parseTask(HttpExchange exchange) throws IOException {
        try {
            String strBody = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
            return gson.fromJson(strBody, Task.class);
        } catch (IOException e) {
            System.out.println("Во время получения запроса возникла ошибка.");
        } catch (JsonSyntaxException e) {
            sendBadRequest(exchange, "Ошибка в теле запроса");
        }

        return null;
    }

}
