package ru.practicum.javakanban.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.practicum.javakanban.manager.TaskManager;

import static ru.practicum.javakanban.http.Constans.GSON;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {

    public PrioritizedHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) {
        String method = exchange.getRequestMethod();

        if (method.equals("GET")) {
            handleGet(exchange);
        } else {
            sendBadRequest(exchange, "Невалидный запрос");
        }
    }

    public void handleGet(HttpExchange exchange) {
        String prioritizedList = GSON.toJson(taskManager.getPrioritizedTasks());
        sendText(exchange, prioritizedList);
    }
}
