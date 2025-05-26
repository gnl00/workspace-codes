package com.demo.threadpool.micrometer;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 需求：一次性执行 1w 个任务
 * - 在任务执行的过程中需要获取到线程池的实时信息：有多少个线程正在运行，目前是否达到最大线程数，任务队列是否有任务积压
 * - 并且需要知道有多少个任务失败，有多少个任务成功，平均执行时间是多少。
 * JDK 自带的 ThreadPoolExecutor 并未收集和暴露这些信息/未实现这些功能
 */
public class ThreadPoolExecutorPlus implements Executor {

    /*
    micrometer.io 提供了一系列的 Meter，例如 Counter、Timer、Gauge，用于收集不同类型的指标。
    要使用 micrometer，需要创建 MeterRegistry，把相关要监控的 Meter 注册上去，在收集时就会遍历所有的指标，拿到对应的数据。
    用于收集一些变化的数据，例如线程池的队列任务积压数，可能会变大变小，那么就可以使用 Gauge 来收集。
    */
    private final MeterRegistry mr;
    private final String name;
    private final ThreadPoolExecutor poolExecutor;

    public ThreadPoolExecutorPlus(String name,
                                  int corePoolSize,
                                  int maxPoolSize,
                                  int keepAliveTime,
                                  TimeUnit unit,
                                  BlockingQueue<Runnable> workQueue,
                                  ThreadFactory threadFactory,
                                  RejectedExecutionHandler rejectedHandler,
                                  MeterRegistry mr) {
        this.name = name;
        this.poolExecutor = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime, unit, workQueue, threadFactory, rejectedHandler);
        this.mr = mr;

        // metrics
        String tagName = "thread.pool.plus.name";
        Gauge.builder("thread.pool.plus.core.size", this, s -> s.poolExecutor.getCorePoolSize()).tags(tagName, name).register(mr);
        Gauge.builder("thread.pool.plus.max.size", this, s -> s.poolExecutor.getMaximumPoolSize()).tags(tagName, name).register(mr);
        Gauge.builder("thread.pool.plus.active.count", this, s -> s.poolExecutor.getActiveCount()).tags(tagName, name).register(mr);
        Gauge.builder("thread.pool.plus.pool.size", this, s -> s.poolExecutor.getPoolSize()).tags(tagName, name).register(mr);
        Gauge.builder("thread.pool.plus.largest.size", this, s -> s.poolExecutor.getLargestPoolSize()).tags(tagName, name).register(mr);
        Gauge.builder("thread.pool.plus.queue.size", this, s -> s.poolExecutor.getQueue().size()).tags(tagName, name).register(mr);
    }

    @Override
    public void execute(Runnable command) {}
}
