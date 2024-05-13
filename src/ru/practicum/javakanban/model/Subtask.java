package ru.practicum.javakanban.model;

import ru.practicum.javakanban.model.Status;
import ru.practicum.javakanban.model.Task;

public class Subtask extends Task {

    private int epicId;

    public Subtask(String name, String description, Status status) {
        super(name, description, status);
    }

    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(Integer epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", epic_id=" + epicId +
                ", status=" + status +
                '}';
    }
}
