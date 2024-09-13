package ru.practicum.javakanban.http;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.practicum.javakanban.exeptions.NotFoundException;
import ru.practicum.javakanban.manager.TaskManager;
import ru.practicum.javakanban.model.Epic;

import java.io.IOException;
import java.util.Optional;

import static ru.practicum.javakanban.http.Constans.*;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {

    public EpicHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
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
                sendText(exchange, GSON.toJson(taskManager.getAllEpics()));
                break;
            case REQUEST_WITH_ID:
                Optional<Integer> optId = getId(exchange);
                if (validateId(exchange, optId)) {
                    try {
                        sendText(exchange, GSON.toJson(taskManager.getEpic(optId.get())));
                    } catch (NotFoundException e) {
                        sendBadRequest(exchange, e.getMessage());
                    }
                }
                break;
            case REQUEST_WITH_ID_AND_RESOURCE:
                if (!("subtasks".equals(params[3]))) {
                    sendNotFound(exchange);
                }

                Optional<Integer> optEpicId = getId(exchange);

                if (validateId(exchange, optEpicId)) {
                    try {
                        sendText(exchange, GSON.toJson(taskManager.getEpicSubtasks(optEpicId.get())));
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
                    taskManager.createEpic(parseEpic(exchange));
                    sendCreated(exchange);
                } catch (IOException e) {
                    System.out.println("Во время получения запроса возникла ошибка.");
                } catch (IllegalArgumentException e) {
                    sendBadRequest(exchange, e.getMessage());
                }
                break;
            case REQUEST_WITH_ID:
                Optional<Integer> optId = getId(exchange);

                if (validateId(exchange, optId)) {
                    try {
                        taskManager.updateEpic(parseEpic(exchange), optId.get());
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
            case REQUEST_WITHOUT_ID:
                taskManager.deleteAllEpics();
                sendCreated(exchange);
            case REQUEST_WITH_ID:

                Optional<Integer> optId = getId(exchange);

                if (validateId(exchange, optId)) {
                    try {
                        taskManager.deleteEpic(optId.get());
                        sendCreated(exchange);
                    } catch (NotFoundException e) {
                        sendBadRequest(exchange, e.getMessage());
                    }
                }
        }
    }


    public Epic parseEpic(HttpExchange exchange) throws IOException {
        try {
            String strBody = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
            return GSON.fromJson(strBody, Epic.class);
        } catch (IOException e) {
            System.out.println("Во время получения запроса возникла ошибка.");
        } catch (JsonSyntaxException e) {
            sendBadRequest(exchange, "Ошибка в теле запроса");
        }

        return null;
    }
}
