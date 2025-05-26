package com.demo.threadpool.tp_handwrite.core;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class WorkerThread extends Thread {

    // 阻塞队列
    private BlockingQueue<Runnable> taskQueue;
    // 空闲时长
    private Long keepAliveTime;
    // 从队列中取出任务并执行的线程
    List<WorkerThread> workerThreads;

    public WorkerThread(BlockingQueue<Runnable> taskQueue, Long keepAliveTime, List<WorkerThread> workerThreads) {
        this.taskQueue = taskQueue;
        this.keepAliveTime = keepAliveTime;
        this.workerThreads = workerThreads;
    }

    @Override
    public void run() {
        while (!currentThread().isInterrupted() && !taskQueue.isEmpty()) {
            long lastActiveTime = System.currentTimeMillis();

            Runnable task;
            try {
                task = taskQueue.poll(keepAliveTime, TimeUnit.MILLISECONDS);
                if (null != task) {
                    task.run();
                    System.out.printf("线程[%s]执行任务[%s]\n", Thread.currentThread().getName(), task);
                } else if (System.currentTimeMillis() - lastActiveTime > keepAliveTime){
                    // 线程空闲
                    workerThreads.remove(this);
                    System.out.printf("线程[%s]空闲超时，被回收\n", Thread.currentThread().getName());
                    break;
                }
            } catch (InterruptedException e) {
                System.out.printf("\n线程[%s]获取任务异常\n", Thread.currentThread().getName());
                workerThreads.remove(this);
                e.printStackTrace();
                break;
            }
        }
    }
}
