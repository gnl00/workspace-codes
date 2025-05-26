package com.demo.threadpool.tp_handwrite.reject;

import com.demo.threadpool.tp_handwrite.core.ThreadPool;

public class AbortPolicy implements RejectedExecutionHandler {
    @Override
    public void rejectedExecution(Runnable task, ThreadPool threadPool) {
        throw new RuntimeException("Task " + task.toString() + " rejected from " + threadPool.toString());
    }
}
