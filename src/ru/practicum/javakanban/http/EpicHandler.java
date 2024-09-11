package ru.practicum.javakanban.http;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.practicum.javakanban.exeptions.ManagerPrioritizeException;
import ru.practicum.javakanban.exeptions.NotFoundException;
import ru.practicum.javakanban.manager.TaskManager;
import ru.practicum.javakanban.model.Epic;
import ru.practicum.javakanban.model.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {

    public EpicHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        gson = new GsonBuilder()
                .serializeNulls()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        switch (method) {
            case "GET":
                handleGet(exchange);
                break;
            case "POST":
                handlePost(exchange);
                break;
            case "DELETE":
                //handleDelete(exchange);
                break;
            default:
                sendBadRequest(exchange, "Невалидный запрос");
                break;
        }
    }

    public void handleGet(HttpExchange exchange) {
        String[] params = getParams(exchange);

        switch (params.length) {
            case 2:
                String epics = gson.toJson(taskManager.getAllEpics());
                sendText(exchange, epics);
                break;
            case 3:
                Optional<Integer> optId = getId(exchange);

                if (optId.isEmpty()) {
                    sendBadRequest(exchange, "Невереный id эпика");
                } else {
                    try {
                        String epic = gson.toJson(taskManager.getEpic(optId.get()));
                        sendText(exchange, epic);
                    } catch (NotFoundException e) {
                        sendBadRequest(exchange, e.getMessage());
                    }
                }
                break;
            case 4:
                if (!("subtasks".equals(params[3]))) {
                    sendNotFound(exchange);
                }

                Optional<Integer> optEpicId = getId(exchange);

                if (optEpicId.isEmpty()) {
                    sendBadRequest(exchange, "Невереный id эпика");
                } else {
                    try {
                        String epic = gson.toJson(taskManager.getEpicSubtasks(optEpicId.get()));
                        sendText(exchange, epic);
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
                    Epic epic = parseEpic(exchange);
                    taskManager.createEpic(epic);
                    sendCreated(exchange);
                } catch (IOException e) {
                    System.out.println("Во время получения запроса возникла ошибка.");
                } catch (IllegalArgumentException e) {
                    sendBadRequest(exchange, e.getMessage());
                }
                break;
            case 3:
                Optional<Integer> optId = getId(exchange);

                if (optId.isEmpty()) {
                    sendBadRequest(exchange, "Невереный id эпика");
                } else {
                    try {
                        Epic newEpic = parseEpic(exchange);
                        taskManager.updateEpic(newEpic, optId.get());
                        sendCreated(exchange);
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
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
            case 2:
                taskManager.deleteAllEpics();
                sendCreated(exchange);
            case 3:
                Optional<Integer> optId = getId(exchange);

                if (optId.isEmpty()) {
                    sendBadRequest(exchange, "Невереный id задачи");
                } else {
                    try {
                        taskManager.deleteEpic(optId.get());
                        sendCreated(exchange);
                    } catch (IllegalArgumentException e) {
                        sendBadRequest(exchange, e.getMessage());
                    }
                }
        }
    }

    public Epic parseEpic(HttpExchange exchange) throws IOException {
        try {
            String strBody = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
            return gson.fromJson(strBody, Epic.class);
        } catch (IOException e) {
            System.out.println("Во время получения запроса возникла ошибка.");
        } catch (JsonSyntaxException e) {
            sendBadRequest(exchange, "Ошибка в теле запроса");
        }

        return null;
    }
}