package com.demo.threadpool.tp_handwrite;

import java.util.concurrent.BlockingQueue;

public class WorkerThread extends Thread {

    private BlockingQueue<Runnable> taskQueue;

    @Override
    public void run() {
        while (!currentThread().isInterrupted() && !taskQueue.isEmpty()) {

        }
    }
}
