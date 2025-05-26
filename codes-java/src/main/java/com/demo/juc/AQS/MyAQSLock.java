package com.demo.juc.AQS;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;

public class MyAQSLock extends AbstractQueuedSynchronizer {
    @Override
    protected boolean tryAcquire(int state) {
        Thread currentThread = Thread.currentThread();
        int s = getState();
        if (compareAndSetState(0, state)) {
            setExclusiveOwnerThread(currentThread);
            return true;
        } else if (currentThread == getExclusiveOwnerThread()) {
            int nextState = s + state;
            if (nextState < 0) {
                throw new Error("Maximum lock count exceeded");
            }
            setState(nextState);
            return true;
        }
        return false;
    }

    @Override
    protected boolean tryRelease(int arg) {
        return super.tryRelease(arg);
    }

    protected MyAQSLock() {
        super();
    }
}
