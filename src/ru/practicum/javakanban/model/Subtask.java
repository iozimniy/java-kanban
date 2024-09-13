package ru.practicum.javakanban.model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {

    private Integer epicId;

    public Subtask(String name, String description, Duration duration, LocalDateTime startTime) {
        super(name, description, duration, startTime);
        this.status = Status.NEW;
    }


    public Subtask(String name, String description, Status status, Duration duration, LocalDateTime startTime) {
        super(name, description, status, duration, startTime);
    }

    public Subtask(String name, String description, Integer id, Status status, Integer epicId, Duration duration,
                   LocalDateTime startTime) {
        super(name, description, id, status, duration, startTime);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, Integer epicId, Duration duration,
                   LocalDateTime startTime) {
        super(name, description, duration, startTime);
        this.epicId = epicId;
        this.status = Status.NEW;
    }

    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(Integer epicId) {
        this.epicId = epicId;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    @Override
    public String toString() {
        return getType() + "{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", epic_id=" + epicId +
                ", status=" + status +
                ", startTime" + startTime +
                '}';
    }

    @Override
    public String convertToString() {
        return getId().toString() + "," + getType().toString() + "," + getName() + "," +
                getStatus().toString() + "," + getDescription() + "," + getDuration().toMinutes() + "," +
                getStartTime().toString() + "," + getEpicId().toString();
    }
}
