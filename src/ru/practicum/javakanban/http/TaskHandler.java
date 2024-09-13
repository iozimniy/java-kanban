package ru.practicum.javakanban.http;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.practicum.javakanban.exeptions.ManagerPrioritizeException;
import ru.practicum.javakanban.exeptions.NotFoundException;
import ru.practicum.javakanban.manager.TaskManager;
import ru.practicum.javakanban.model.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {



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
                handleDelete(exchange);
                break;
            default:
                sendBadRequest(exchange, "Невалидный запрос");
                break;
        }
    }

    public void handleGet(HttpExchange exchange) {
        String[] params = getParams(exchange);

        switch (params.length) {
            case REQUEST_WITHOUT_ID:
                String tasks = gson.toJson(taskManager.getAllTasks());
                sendText(exchange, tasks);
                break;
            case REQUEST_WITH_ID:
                Optional<Integer> optId = getId(exchange);

                if (validateId(exchange, optId)) {
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
            case REQUEST_WITHOUT_ID:
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
            case REQUEST_WITH_ID:
                Optional<Integer> optId = getId(exchange);

                if (validateId(exchange, optId)) {
                    try {
                        Task newTask = parseTask(exchange);
                        taskManager.updateTask(newTask, optId.get());
                        sendCreated(exchange);
                    } catch (IllegalArgumentException e) {
                        sendBadRequest(exchange, e.getMessage());
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    } catch (ManagerPrioritizeException e) {
                        sendHasInteractions(exchange);
                    }
                }
                break;
            default:
                sendNotFound(exchange);
                break;
        }
    }

    public void handleDelete(HttpExchange exchange) {
        String[] params = getParams(exchange);

        switch (params.length) {
            case REQUEST_WITHOUT_ID:
                taskManager.deleteAllTasks();
                sendCreated(exchange);
            case REQUEST_WITH_ID:
                Optional<Integer> optId = getId(exchange);

                if (validateId(exchange, optId)) {
                    try {
                        taskManager.deleteTask(optId.get());
                        sendCreated(exchange);
                    } catch (IllegalArgumentException e) {
                        sendBadRequest(exchange, e.getMessage());
                    }
                }
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
