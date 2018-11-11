/**
 * 
 */
package org.lingg.redis.reentrantlock.io.lock.test;

import com.alibaba.fastjson.JSON;
import org.junit.jupiter.api.Test;
import org.lingg.redis.reentrantlock.io.lock.redis.util.LockInfo;
import org.lingg.redis.reentrantlock.io.lock.redis.util.ReadWriteLockInfo;

import java.util.HashSet;
import java.util.Set;

/**
 * @author lixiaohui
 * @date 2016年9月14日 上午10:25:46
 * 
 */
public class FastJsonTest {
	
	@Test
	public void test() {
		User user = new User();
		user.setName("lixiaohui");
		String json = JSON.toJSONString(user);
		System.out.println(json);
	}
	
	@Test
	public void test1() {
		Set<LockInfo> infos = new HashSet<LockInfo>();
		infos.add(LockInfo.newForCurrThread(System.currentTimeMillis()));
		infos.add(LockInfo.newForCurrThread(System.currentTimeMillis()));
		
		User user = new User();
		user.setName("lll");
		user.setInfos(infos);
		
		System.out.println(JSON.toJSONString(user));
	}
	
	@Test
	public void test2() {
		String json = "{\"infos\":[{\"count\":1,\"currentThread\":true,\"expires\":1474086421769,\"jvmPid\":11636,\"mac\":\"28-D2-44-0E-0D-9A\",\"threadId\":1},{\"count\":1,\"currentThread\":true,\"expires\":1474086421658,\"jvmPid\":11636,\"mac\":\"28-D2-44-0E-0D-9A\",\"threadId\":1}],\"name\":\"lll\"}";
		User user = JSON.parseObject(json, User.class);
		System.out.println(user);
	}
	
	@Test
	public void test3() {
		Set<LockInfo> readInfos = new HashSet<LockInfo>();
		readInfos.add(LockInfo.newForCurrThread(System.currentTimeMillis()));
		
		ReadWriteLockInfo info = new ReadWriteLockInfo();
		info.setReadInfos(readInfos);
		info.setWriteInfo(LockInfo.newForCurrThread(System.currentTimeMillis()));
		
		System.out.println(info.toString());
		System.out.println(info.isLegal());
	}
}
