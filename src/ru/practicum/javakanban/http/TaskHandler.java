package ru.practicum.javakanban.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.practicum.javakanban.exeptions.NotFoundException;
import ru.practicum.javakanban.manager.TaskManager;

import java.util.Optional;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {

    TaskManager taskManager;

    public TaskHandler(TaskManager taskManager) {
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

                break;
            case "DELETE":

                break;
            default:

                break;
        }
    }

    public void handleGet(HttpExchange exchange) {
        String[] params = getParams(exchange);

        Gson gson = new GsonBuilder()
                .serializeNulls()
                .create();

        switch (params.length) {
            case 2:
                String tasks = gson.toJson(taskManager.getAllTasks());
                sendText(exchange, tasks);
                break;
            case 3:
                Optional<Integer> optId = getId(exchange);

                if (optId.isEmpty()) {
                    sendBadId(exchange);
                } else {
                    try {
                        String task = gson.toJson(taskManager.getTask(optId.get()));
                        sendText(exchange, task);
                    } catch (NotFoundException e) {
                        sendBadId(exchange);
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

        Gson gson = new GsonBuilder()
                .serializeNulls()
                .create();

        //тут switch и дальше в зависимости от количества параметров.
    }
}
