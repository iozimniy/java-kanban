package ru.practicum.javakanban.http;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public abstract class BaseHttpHandler {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    protected void sendText(HttpExchange exchange, String answer) {
        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(200, 0);
            os.write(answer.getBytes(DEFAULT_CHARSET));
        } catch (IOException e) {
            System.out.println("Во время отправки ответа возникла ошибка");
        }
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
            System.out.println("Во время отправки ответа возникла ошибка");
        }
    }

    protected void sendBadId(HttpExchange exchange) {
        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(400, 0);
            os.write("Задача не найдена по id".getBytes(DEFAULT_CHARSET));
        } catch (IOException e) {
            System.out.println("Во время отправки ответа возникла ошибка");
        }
    }

    protected String[] getParams(HttpExchange exchange) {
        String path = exchange.getRequestURI().getPath();
        return path.split("/");
    }
}
