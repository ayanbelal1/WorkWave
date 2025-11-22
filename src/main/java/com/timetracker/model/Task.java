package com.timetracker.model;

import java.time.LocalDate;

public class Task {
    private int id;
    private String title;
    private String description;
    private int projectId;
    private int assignedUserId;
    private TaskStatus status;
    private LocalDate dueDate;
    private int estimatedHours;

    public Task() {}

    public Task(String title, String description, int projectId, int assignedUserId, LocalDate dueDate, int estimatedHours) {
        this.title = title;
        this.description = description;
        this.projectId = projectId;
        this.assignedUserId = assignedUserId;
        this.dueDate = dueDate;
        this.estimatedHours = estimatedHours;
        this.status = TaskStatus.TODO;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getProjectId() { return projectId; }
    public void setProjectId(int projectId) { this.projectId = projectId; }

    public int getAssignedUserId() { return assignedUserId; }
    public void setAssignedUserId(int assignedUserId) { this.assignedUserId = assignedUserId; }

    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public int getEstimatedHours() { return estimatedHours; }
    public void setEstimatedHours(int estimatedHours) { this.estimatedHours = estimatedHours; }

    public enum TaskStatus {
        TODO("To Do"),
        IN_PROGRESS("In Progress"),
        COMPLETED("Completed"),
        BLOCKED("Blocked");

        private final String displayName;

        TaskStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }
}