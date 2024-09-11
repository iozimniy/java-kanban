package ru.practicum.javakanban.http;

import com.sun.net.httpserver.HttpServer;
import ru.practicum.javakanban.manager.Managers;
import ru.practicum.javakanban.manager.TaskManager;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    static TaskManager taskManager;
    HttpServer httpServer;

    private static final int PORT = 8080;
    File file = File.createTempFile("taskManagerCsv", ".csv");

    public HttpTaskServer() throws IOException {
        taskManager = Managers.getFileBacked(file);
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
    }

    public void startServer() {
        httpServer.start();
        System.out.println("Старт сервера");
    }

    public void stopServer() {
        httpServer.stop(1);
    }

    public void createHandlers() {
        httpServer.createContext("/tasks", new TaskHandler(taskManager));
    }

    public static void main(String[] args) {
        try {
            HttpTaskServer taskServer = new HttpTaskServer();
            taskServer.createHandlers();
            taskServer.startServer();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
