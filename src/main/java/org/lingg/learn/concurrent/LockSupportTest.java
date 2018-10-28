package org.lingg.learn.concurrent;

import java.util.concurrent.locks.LockSupport;

public class LockSupportTest {
	public static void main(String[] args) throws Exception {
//		TestLockSupport lockSupport = new TestLockSupport();
//		lockSupport.start();
//		TestThread thread = new TestThread();
//		thread.start();

		Thread t2 = new Thread() {
			public void run() {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("t2 end sleep");
				LockSupport.park(this);
				System.out.println("t2 : I am alive");
			};
		};

		Thread t1 = new Thread() {
			public void run() {
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				LockSupport.unpark(t2);
				System.out.println("t1 : I am done");
			};
		};
		t1.start();
		t2.start();
	}
}
class TestLockSupport extends Thread {
	public void run() {
		System.out.println( "TestLockSupport.run()" );
		LockSupport.park(  );
	}
}

class TestThread extends Thread {
	public void run() {
		System.out.println( "TestThread.run()" );
		synchronized (this) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
