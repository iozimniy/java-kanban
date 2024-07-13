package ru.practicum.javakanban.model;

public class Subtask extends Task {

    private final TaskType type = TaskType.SUBTASK;
    private Integer epicId;

    public Subtask(String name, String description, Status status) {
        super(name, description, status);
    }

    public Subtask(String name, String description) {
        super(name, description);
        status = Status.NEW;
    }

    public Subtask(String name, String description, Integer id, Status status, TaskType type, Integer epicId) {
        super(name, description, id, status, type);
        this.epicId = epicId;
    }

    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(Integer epicId) {
        this.epicId = epicId;
    }

    @Override
    public TaskType getType() {
        return type;
    }

    @Override
    public String toString() {
        return getType() + "{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", epic_id=" + epicId +
                ", status=" + status +
                '}';
    }

    @Override
    public String convertToString() {
        return getId().toString() + "," + getType().toString() + "," + getName() + "," + getStatus().toString() + ","
                + getDescription() + "," + getEpicId().toString();
    }
}
