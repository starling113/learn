package org.lingg.learn.zookeeper.curator.lock;

import java.util.concurrent.atomic.AtomicBoolean;

//创建一个模拟的共享资源， 这个资源期望只能单线程的访问，否则会有并发问题。
public class FakeLimitedResource {
    private final AtomicBoolean inUse = new AtomicBoolean(false);

    public void use() throws InterruptedException {
        // 真实环境中我们会在这里访问/维护一个共享的资源
        //这个例子在使用锁的情况下不会非法并发异常IllegalStateException
        //但是在无锁的情况由于sleep了一段时间，很容易抛出异常
        if (!inUse.compareAndSet(false, true)) {
            throw new IllegalStateException("Needs to be used by one client at a time");
        }
        try {
            Thread.sleep((long) (3 * Math.random()));
        } finally {
            inUse.set(false);
        }
    }
}

//作者：zhrowable
//链接：https://www.jianshu.com/p/70151fc0ef5d
//來源：简书
//简书著作权归作者所有，任何形式的转载都请联系作者获得授权并注明出处。