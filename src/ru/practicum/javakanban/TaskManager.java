package ru.practicum.javakanban;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
     private int idCounter = 0;
     private HashMap<Integer, Task> tasks = new HashMap<>();
     private HashMap<Integer, Epic> epics = new HashMap<>();
     private HashMap<Integer, Subtask> subtasks = new HashMap<>();

     public void createTask(Task task) {
          task.setId(idCounter++);
          tasks.put(task.getId(), task);
     }

     public void createEpic(Epic epic) {
          epic.setId(idCounter++);
          epics.put(epic.getId(), epic);
     }

     public void createSubtask(Subtask subtask, int epicId) {
          subtask.setId(idCounter++);
          Epic epic = epics.get(epicId);

          ArrayList<Subtask> epicSubtasks = epic.getSubtasks();
          epicSubtasks.add(subtask);

          subtask.setEpic_id(epic.getId());
          subtasks.put(subtask.getId(), subtask);

          epic.updateStatus();
          updateEpic(epic);
     }

     public void updateTask(Task task) {
          tasks.put(task.getId(), task);
     }

     public void updateEpic(Epic epic) {
          epics.put(epic.getId(), epic);
     }

     public void updateSubtask(Subtask subtask) {
          subtasks.put(subtask.getId(), subtask);
          Epic epic = epics.get(subtask.getEpic_id());

          epic.updateStatus();
          updateEpic(epic);
     }

     public ArrayList<Task> getAllTasks() {
         return new ArrayList<>(tasks.values());
     }

     public ArrayList<Epic> getAllEpics() {
         return new ArrayList<>(epics.values());
     }

     public ArrayList<Subtask> getAllSubtasks() {
         return new ArrayList<>(subtasks.values());
     }

     public ArrayList<Subtask> getEpicSubtasks(int id) {
          if (epics.containsKey(id)) {
               Epic epic = epics.get(id);
               return epic.getSubtasks();
          }

          return null;
     }

     public void deleteAllTasks() {
          tasks.clear();
     }

     public void deleteAllEpics() {
          subtasks.clear();
          epics.clear();
     }

     public void deleteAllSubtasks() {

          for (Epic epic : epics.values()) {
               epic.clearSubtasks();
               epic.updateStatus();
               updateEpic(epic);
          }

          subtasks.clear();
     }

     public Task getTask(int id) {
          if (tasks.containsKey(id)) {
               return tasks.get(id);
          }

          return null;
     }

     public Epic getEpic(int id) {
          if (epics.containsKey(id)) {
               return epics.get(id);
          }

          return null;
     }

     public Subtask getSubtask(int id) {
          if (subtasks.containsKey(id)) {
               return subtasks.get(id);
          }

          return null;
     }

     public void deleteTask(int id) {
          tasks.remove(id);
     }

     public void deleteEpic(int id) {
          Epic epic = epics.get(id);
          ArrayList<Subtask> epicSubtasks = epic.getSubtasks();

          for (Subtask subtask : epicSubtasks) {
              subtasks.remove(subtask.getId());
          }

          epics.remove(id);
     }

     public void deleteSubtask(int id) {
          if (subtasks.containsKey(id)) {
               Subtask subtask = subtasks.get(id);
               Epic epic = epics.get(subtask.getEpic_id());

               epic.removeSubtask(subtask);
               epic.updateStatus();
               subtasks.remove(id);
               updateEpic(epic);
          }
     }
}