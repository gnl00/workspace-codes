package com.demo.threadpool.tp_handwrite.core;

import com.demo.threadpool.tp_handwrite.reject.AbortPolicy;
import com.demo.threadpool.tp_handwrite.reject.RejectedExecutionHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class AThreadPool implements ThreadPool {

    private Integer initialSize;
    private Integer coreSize;
    private Integer maxSize;
    private Integer queueSize;
    private BlockingQueue<Runnable> taskQueue;
    private List<WorkerThread> workerThreads;
    private final RejectedExecutionHandler rejectHandler;
    private Long keepAliveTime = 1000L * 60;

    private volatile boolean isShutdown;

    private final static RejectedExecutionHandler DEFAULT_REJECTED_HANDLER = new AbortPolicy();

    public AThreadPool(Integer initialSize, Integer coreSize, Integer maxSize, Integer queueSize, Long keepAliveTime) {
        this(initialSize, coreSize, maxSize, queueSize, keepAliveTime, DEFAULT_REJECTED_HANDLER);
    }

    public AThreadPool(Integer initialSize, Integer coreSize, Integer maxSize, Integer queueSize, Long keepAliveTime, RejectedExecutionHandler rejectHandler) {
        this.rejectHandler = rejectHandler;
        this.initialSize = initialSize;
        this.coreSize = coreSize;
        this.maxSize = maxSize;
        this.queueSize = queueSize;
        this.keepAliveTime = keepAliveTime;
        this.taskQueue = new LinkedBlockingQueue<>(queueSize);
        this.workerThreads = new ArrayList<>(initialSize);
        // 初始化工作线程
        for (int i = 0; i < initialSize; i++) {
            WorkerThread workerThread = new WorkerThread(taskQueue, keepAliveTime, workerThreads);
            workerThread.start();
            workerThreads.add(workerThread);
        }
    }

    @Override
    public void execute(Runnable task) {
        if (isShutdown) {
            throw new IllegalStateException("线程池已经关闭");
        }
        RunnableWrapper wrapper = (RunnableWrapper) task;
        System.out.printf("获取任务: %s %n" , wrapper.getTaskId());

        // 当线程数量小于核心线程数时，创建新的线程
        if (workerThreads.size() < coreSize) {
            addWorkerThread(task);
            System.out.printf("小于核心线程数，创建新的线程, =====> 当前线程数: %d, 队列剩余容量: %d%n" ,workerThreads.size(), taskQueue.remainingCapacity());
        } else if (!taskQueue.offer(task)) {
            // 当任务队列已满时且线程数量小于最大线程数时，创建新的线程
            if (workerThreads.size() < maxSize) {
                addWorkerThread(task);
                System.out.printf("队列已满，创建新的线程, =====> 当前线程数: %d, 队列剩余容量: %d%n" ,workerThreads.size(), taskQueue.remainingCapacity());
            } else {
                // 执行拒绝策略
                rejectHandler.rejectedExecution(task, this);
            }
        } else {
            System.out.printf("任务加入队列, =====> 当前线程数: %d, 队列剩余容量: %d%n" ,workerThreads.size(), taskQueue.remainingCapacity());
        }
    }

    /**
     * 添加新的工作线程,并执行任务
     */
    private void addWorkerThread(Runnable task) {
        WorkerThread workerThread = new WorkerThread(taskQueue, keepAliveTime, workerThreads);
        workerThreads.add(workerThread);
        workerThread.start();
        // 将任务加入到任务队列中
        try {
            taskQueue.put(task);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void shutdown() {
        System.out.printf("关闭线程池, 线程个数: %d, 队列剩余容量: %d%n" ,workerThreads.size(), taskQueue.remainingCapacity());
        isShutdown = true;
        for (WorkerThread workerThread : workerThreads) {
            // 中断线程
            workerThread.interrupt();
        }
    }

    @Override
    public List<Runnable> shutdownNow() {
        isShutdown = true;
        // 清空队列
        List<Runnable> remainingTasks = new ArrayList<>();
        taskQueue.drainTo(remainingTasks);
        // 中断所有线程
        for (WorkerThread workerThread : workerThreads) {
            // 中断线程
            workerThread.interrupt();
        }
        // 返回未执行的任务
        return remainingTasks;
    }
}
