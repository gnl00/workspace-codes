package com.demo.threadpool.tp_handwrite.reject;

import com.demo.threadpool.tp_handwrite.core.RunnableWrapper;
import com.demo.threadpool.tp_handwrite.core.ThreadPool;

public class DiscardPolicy implements RejectedExecutionHandler {
    @Override
    public void rejectedExecution(Runnable task, ThreadPool threadPool) {
        RunnableWrapper wrapper = (RunnableWrapper) task;
        System.out.println("Task rejected: " + wrapper.getTaskId());
    }
}
