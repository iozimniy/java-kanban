package ru.practicum.javakanban.http;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.practicum.javakanban.exeptions.ManagerPrioritizeException;
import ru.practicum.javakanban.exeptions.NotFoundException;
import ru.practicum.javakanban.manager.TaskManager;
import ru.practicum.javakanban.model.Subtask;

import java.io.IOException;
import java.util.Optional;

import static ru.practicum.javakanban.http.Constans.GSON;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {

    SubtaskHandler(TaskManager taskManager) {
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
                String subtasks = GSON.toJson(taskManager.getAllSubtasks());
                sendText(exchange, subtasks);
                break;
            case REQUEST_WITH_ID:
                Optional<Integer> optId = getId(exchange);

                if (validateId(exchange, optId)) {
                    try {
                        String subtask = GSON.toJson(taskManager.getSubtask(optId.get()));
                        sendText(exchange, subtask);
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
                    Subtask subtask = parseSubtask(exchange);
                    taskManager.createSubtask(subtask, subtask.getEpicId());
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
                        Subtask newSubtask = parseSubtask(exchange);
                        taskManager.updateSubtask(newSubtask, optId.get());
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
                taskManager.deleteAllSubtasks();
                sendCreated(exchange);
            case REQUEST_WITH_ID:
                Optional<Integer> optId = getId(exchange);

                if (validateId(exchange, optId)) {
                    try {
                        taskManager.deleteSubtask(optId.get());
                        sendCreated(exchange);
                    } catch (IllegalArgumentException e) {
                        sendBadRequest(exchange, e.getMessage());
                    }
                }
        }
    }

    public Subtask parseSubtask(HttpExchange exchange) throws IOException {
        try {
            String strBody = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
            return GSON.fromJson(strBody, Subtask.class);
        } catch (IOException e) {
            System.out.println("Во время получения запроса возникла ошибка.");
        } catch (JsonSyntaxException e) {
            sendBadRequest(exchange, "Ошибка в теле запроса");
        }

        return null;
    }
}
