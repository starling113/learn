package org.lingg.redis.reentrantlock.io.lock.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;


import org.junit.jupiter.api.Test;
import org.lingg.learn.redisInAction.book.RedisConst;
import org.lingg.redis.reentrantlock.io.lock.redis.RedisReentrantLock;
import redis.clients.jedis.Jedis;

/**
 * @author lixiaohui
 * @date 2016年9月28日 下午8:41:36
 * 
 */
public class ReentrantTest {
	
	@Test
	public void test() throws Exception {
		// 创建5个线程不停地去重入(随机次数n, 0 <= n <=5)获取锁
		List<Thread> threads = createThreads(5);
		//开始任务
		for (Thread t : threads) {
			t.start();
		}
		// 执行60秒
		Thread.sleep(60 * 1000);
		//停止所有线程
		Task.alive = false;
		// 等待所有线程终止
		for (Thread t : threads) {
			t.join();
		}
		
	}
	// 创建count个线程，每个线程都是不同的jedis连接以及不同的与时间服务器的连接
	private List<Thread> createThreads(int count) throws IOException {
		List<Thread> threads = new ArrayList<Thread>();
		for (int i = 0; i < count; i++) {
			Jedis jedis = new Jedis(RedisConst.redisHost, 6379);
			RedisReentrantLock lock = new RedisReentrantLock(jedis);
			Task task = new Task(lock);
			Thread t = new Thread(task);
			threads.add(t);
		}
		return threads;
	}
	
	private static class Task implements Runnable {
		
		private RedisReentrantLock lock;
		
		private final int MAX_ENTRANT = 5;
		
		private final Random random = new Random();
		
		private static boolean alive = true;
		
		Task(RedisReentrantLock lock) {
			this.lock = lock;
		}
		
		public void run() {
			while (alive) {
				int times = random.nextInt(MAX_ENTRANT);
				doLock(times);
			}
		}
		
		private void doLock(int times) {
			if (lock.tryLock(5, TimeUnit.SECONDS)) {
				try {
					if (times > 0) {
						doLock(--times);
					}
				} finally {
					if (lock != null) {
						lock.unlock();
					}
				}
			}
			
		}
		
		
		
	}
	
}
