package org.lingg.guava;

import com.google.common.util.concurrent.RateLimiter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

//参考网站 https://blog.csdn.net/tianyaleixiaowu/article/details/74942405

//http://www.cnblogs.com/xuwc/p/9123078.html

//限流  令牌桶算法
public class RateLimiterTest {
    static int index = 0;
    /**
     * 创建一个稳定输出令牌的RateLimiter，保证了平均每秒不超过permitsPerSecond个请求
     * 当请求到来的速度超过了permitsPerSecond，保证每秒只处理permitsPerSecond个请求
     * 当这个RateLimiter使用不足(即请求到来速度小于permitsPerSecond)，会囤积最多permitsPerSecond个请求
     */
    final static RateLimiter rateLimiter = RateLimiter.create(2); //每秒放2个令牌，即每秒可以处理2个请求

    /**
     * 创建一个稳定输出令牌的RateLimiter，保证了平均每秒不超过permitsPerSecond个请求
     * 还包含一个热身期(warmup period),热身期内，RateLimiter会平滑的将其释放令牌的速率加大，直到起达到最大速率
     * 同样，如果RateLimiter在热身期没有足够的请求(unused),则起速率会逐渐降低到冷却状态
     * <p>
     * 设计这个的意图是为了满足那种资源提供方需要热身时间，而不是每次访问都能提供稳定速率的服务的情况(比如带缓存服务，需要定期刷新缓存的)
     * 参数warmupPeriod和unit决定了其从冷却状态到达最大速率的时间
     */
    final RateLimiter rateLimiter2 = RateLimiter.create(100, 5, TimeUnit.SECONDS);

    public static void main(String[] args) {

        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            tasks.add(() -> {
                        index++;
                        if (index == 10) {
                            System.err.println(rateLimiter.getRate());
                            rateLimiter.setRate(0.25); // 调整速率  每秒0.25个，即4秒通过一个请求
                            System.err.println(rateLimiter.getRate());
                        }
                        System.out.println(LocalDateTime.now() + " " + Thread.currentThread().getName());
                    }
            );
        }

        Executor executor = Executors.newCachedThreadPool();

        RateLimiterTest test = new RateLimiterTest();
        test.submitTasks(tasks, executor);
    }

    void submitTasks(List<Runnable> tasks, Executor executor) {
        for (Runnable task : tasks) {
            //blocking until the request can be granted
            rateLimiter.acquire(); // 阻塞等待获取令牌  tryAcquire方法来进行无阻塞或可超时的令牌消费
            executor.execute(task);
        }
    }
}
