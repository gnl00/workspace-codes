package com.demo.threadpool.tp_handwrite.core;

import lombok.Data;

@Data
public class RunnableWrapper implements Runnable {

    private final Integer taskId;

    public RunnableWrapper(Integer taskId) {
        this.taskId = taskId;
    }

    @Override
    public void run() {
        System.out.println("Task " + this.taskId + " is running.");
        try {
            Thread.sleep(100);
        } catch (Exception e) {
            e.printStackTrace();
            // ignore
        }
        System.out.println("Task " + this.taskId + " is completed.");
    }
}
