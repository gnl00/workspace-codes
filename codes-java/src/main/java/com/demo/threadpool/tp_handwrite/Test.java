package com.demo.threadpool.tp_handwrite;

import com.demo.threadpool.tp_handwrite.core.AThreadPool;
import com.demo.threadpool.tp_handwrite.core.RunnableWrapper;
import com.demo.threadpool.tp_handwrite.reject.DiscardPolicy;

public class Test {
    public static void main(String[] args) throws InterruptedException {
        AThreadPool threadPool = new AThreadPool(1, 4, 2, 3, 2000L, new DiscardPolicy());
        for (int i = 0; i < 10; i++) {
            threadPool.execute(new RunnableWrapper(i));
        }
        Thread.sleep(10_000);
        threadPool.shutdown();
    }
}
