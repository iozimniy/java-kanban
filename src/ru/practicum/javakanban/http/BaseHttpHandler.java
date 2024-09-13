package ru.practicum.javakanban.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.practicum.javakanban.manager.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public abstract class BaseHttpHandler {
    protected static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    protected static final int REQUEST_WITHOUT_ID = 2;
    protected static final int REQUEST_WITH_ID = 3;
    protected static final int REQUEST_WITH_ID_AND_RESOURCE = 4;
    TaskManager taskManager;
    Gson gson;

    protected void sendText(HttpExchange exchange, String answer) {
        exchange.getResponseHeaders().add("Content-type", "application/json;charset=utf-8");
        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(200, 0);
            os.write(answer.getBytes(DEFAULT_CHARSET));
        } catch (IOException e) {
            System.out.println("Во время отправки ответа возникла ошибка");
        }

        exchange.close();
    }

    protected Optional<Integer> getId(HttpExchange exchange) {
        String[] params = exchange.getRequestURI().getPath().split("/");

        try {
            return Optional.of(Integer.parseInt(params[2]));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    protected void sendNotFound(HttpExchange exchange) {
        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(404, 0);
            os.write("Запрашиваемый ресурс не существует".getBytes(DEFAULT_CHARSET));
        } catch (IOException e) {
            System.out.println("Во время отправки ответа возникла ошибка.");
        }

        exchange.close();
    }

    protected void sendBadRequest(HttpExchange exchange, String message) {
        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(404, 0);
            os.write(message.getBytes(DEFAULT_CHARSET));
        } catch (IOException e) {
            System.out.println("Во время отправки ответа возникла ошибка.");
        }

        exchange.close();
    }

    protected void sendHasInteractions(HttpExchange exchange) {
        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(406, 0);
            os.write("Задача пересекается по времени с другой задачей.".getBytes(DEFAULT_CHARSET));
        } catch (IOException e) {
            System.out.println("Во время отправки ответа возникла ошибка.");

        }

        exchange.close();
    }

    protected void sendCreated(HttpExchange exchange) {
        try {
            exchange.sendResponseHeaders(201, 0);
        } catch (IOException e) {
            System.out.println("Во время отправки ответа возникла ошибка.");
        }

        exchange.close();
    }

    protected String[] getParams(HttpExchange exchange) {
        String path = exchange.getRequestURI().getPath();
        return path.split("/");
    }

    public boolean validateId(HttpExchange exchange, Optional<Integer> optId) {
        if (optId.isEmpty()) {
            sendBadRequest(exchange, "Невереный id");
            return false;
        }

        return true;
    }
}
