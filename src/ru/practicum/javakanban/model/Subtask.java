package ru.practicum.javakanban.model;

public class Subtask extends Task {

    private Integer epicId;

    public Subtask(String name, String description, Status status) {
        super(name, description, status);
    }

    public Subtask(String name, String description) {
        super(name, description);
        status = Status.NEW;
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
