package com.demo.threadpool.tp_handwrite.reject;

import com.demo.threadpool.tp_handwrite.core.ThreadPool;

public interface RejectedExecutionHandler {
    void rejectedExecution(Runnable task, ThreadPool threadPool);
}
