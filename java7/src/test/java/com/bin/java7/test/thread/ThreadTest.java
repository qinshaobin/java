package com.bin.java7.test.thread;

import org.junit.Test;

/**
 * 实现多线程的两种方式：	
 *	1、继承 Thread类 重写run方法
 *	2、实现 Runnable接口
 */
public class ThreadTest {

	@Test
	public void testThread1()
	{
		Thread1 thread1 = new Thread1();
		thread1.start();
	}

	@Test
	public void testThread2()
	{
		new Thread(new Thread2()).start();
	}

	private static class Thread1 extends Thread{
		
		@Override
		public void run() {
			System.out.println(Thread.currentThread());
		}
	}
	
	private static class Thread2 implements Runnable{

		@Override
		public void run() {
			System.out.println(Thread.currentThread());
		}
	}
}
