package ru.practicum.javakanban.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.practicum.javakanban.manager.TaskManager;

import static ru.practicum.javakanban.http.Constans.GSON;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    public HistoryHandler(TaskManager taskManager) {
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
        String history = GSON.toJson(taskManager.getHistory());
        sendText(exchange, history);
    }
}
