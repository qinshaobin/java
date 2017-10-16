package com.bin.java7.test.thread;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * http://blog.csdn.net/luoweifu/article/details/46613015
 *
 *synchronized是Java中的关键字，是一种同步锁。它修饰的对象有以下几种：
1. 修饰一个代码块，被修饰的代码块称为同步语句块，其作用的范围是大括号{}括起来的代码，作用的对象是调用这个代码块的对象；
2. 修饰一个方法，被修饰的方法称为同步方法，其作用的范围是整个方法，作用的对象是调用这个方法的对象；
3. 修改一个静态的方法，其作用的范围是整个静态方法，作用的对象是这个类的所有对象；
4. 修改一个类，其作用的范围是synchronized后面括号括起来的部分，作用主的对象是这个类的所有对象。

A. 无论synchronized关键字加在方法上还是对象上，如果它作用的对象是非静态的，则它取得的锁是对象；
	如果synchronized作用的对象是一个静态方法或一个类，则它取得的锁是对类，该类所有的对象同一把锁。
B. 每个对象只有一个锁（lock）与之相关联，谁拿到这个锁谁就可以运行它所控制的那段代码。
C. 实现同步是要很大的系统开销作为代价的，甚至可能造成死锁，所以尽量避免无谓的同步控制。
 */
public class SynchronizedTest {

	//为了在junit中跑多线程,只有把主线程堵塞
	private static CountDownLatch countDownLatch;

	public void init(int count)
	{
		countDownLatch = new CountDownLatch(count);
	}

	/**
	 * 线程锁住同一个对象实例
	 */
	@Test
	public void testSynchronized1() throws InterruptedException
	{
		init(2);
		SyncThread1 syncThread = new SyncThread1();
		new Thread(syncThread).start();
		new Thread(syncThread).start();
		countDownLatch.await();
	}

	/**
	 * 线程锁住不同对象实例静态变量
	 */
	@Test
	public void testSynchronized2() throws InterruptedException
	{
		init(2);
		new Thread(new SyncThread1()).start();
		new Thread(new SyncThread1()).start();
		countDownLatch.await();
	}

	/**
	 * 线程锁住同一个对象实例
	 */
	@Test
	public void testSynchronized3() throws InterruptedException
	{
		init(2);
		new Thread(new SyncThread2()).start();
		new Thread(new SyncThread2()).start();
		countDownLatch.await();
	}

	/**
	 * 死锁
	 */
	@Test
	public void testLock() throws InterruptedException
	{
		init(2);
		new Thread(new DateLockThread(Boolean.TRUE)).start();
		new Thread(new DateLockThread(Boolean.FALSE)).start();
		countDownLatch.await();
	}

	private static class SyncThread1 implements Runnable{
		
		private static int count = 0;
		
		private final Object lock = new Object();
		
		@Override
		public void run() {
			synchronized (lock) {
				for(int i=0; i<5; i++)
				{
					System.out.println(Thread.currentThread().getName() + ":" + count++);
					try {
						TimeUnit.MILLISECONDS.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				SynchronizedTest.countDownLatch.countDown();
			}
		}
	}
	
	private static class SyncThread2 implements Runnable{
		
		private static int count = 0;
		
		private final static Object lock = new Object();
		
		@Override
		public void run() {
			synchronized (lock) {
				for(int i=0; i<5; i++)
				{
					System.out.println(Thread.currentThread().getName() + ":" + count++);
					try {
						TimeUnit.MILLISECONDS.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				SynchronizedTest.countDownLatch.countDown();
			}
		}
	}
	
	private static class DateLockThread implements Runnable{
		
		private final static Object lock1 = new Object();
		
		private final static Object lock2 = new Object();
		
		private boolean flag;
		
		private DateLockThread(boolean flag) {
			this.flag = flag;
		}

		@Override
		public void run() {
			if(flag)
			{
				synchronized (lock1) {
					try {
						System.out.println("线程1锁住lock1,打算继续锁住lock2");
						TimeUnit.SECONDS.sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					synchronized (lock2) {
						System.out.println("线程1锁住lock2");
					}
				}
			}
			else
			{
				synchronized (lock2) {
					try {
						System.out.println("线程2锁住lock2,打算继续锁住lock1");
						TimeUnit.SECONDS.sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					synchronized (lock1) {
						System.out.println("线程2锁住lock1");
					}
				}
			}
			SynchronizedTest.countDownLatch.countDown();
		}
	}
}
