package ru.practicum.javakanban.http;

import com.sun.net.httpserver.HttpServer;
import ru.practicum.javakanban.manager.Managers;
import ru.practicum.javakanban.manager.TaskManager;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    TaskManager taskManager;
    HttpServer httpServer;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
    }

    public static void main(String[] args) {

        try {
            File file = File.createTempFile("taskManagerCsv", ".csv");
            HttpTaskServer taskServer = new HttpTaskServer(Managers.getFileBacked(file));
            taskServer.createHandlers();
            taskServer.startServer();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        httpServer.createContext("/epics", new EpicHandler(taskManager));
        httpServer.createContext("/sabtasks", new SubtaskHandler(taskManager));
    }
}
