package com.aman;

public class Task {
    private String taskName;
    private int timeToComplete;

    public Task(String taskName, int timeToComplete) {
        this.taskName = taskName;
        this.timeToComplete = timeToComplete;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public int getTimeToComplete() {
        return timeToComplete;
    }

    public void setTimeToComplete(int timeToComplete) {
        this.timeToComplete = timeToComplete;
    }
}
