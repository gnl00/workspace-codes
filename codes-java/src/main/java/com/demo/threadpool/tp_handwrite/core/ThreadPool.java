package com.demo.threadpool.tp_handwrite.core;

import java.util.List;

public interface ThreadPool {
    void execute(Runnable task);
    void shutdown();
    List<Runnable> shutdownNow();
}
