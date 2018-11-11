package org.lingg.redis.reentrantlock.io.lock.test;


import org.junit.jupiter.api.Test;
import org.lingg.redis.reentrantlock.io.lock.util.PlatformUtils;

public class PlatformTest {
	
	@Test
	public void test() {
		System.out.println(PlatformUtils.MACAddress());
	}
	
}
