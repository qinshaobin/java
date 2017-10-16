package com.bin.java7.test.thread;

import org.junit.Test;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Lock接口，它提供了比synchronized更加广泛的锁定操作。Lock接口有3个实现它的类：
 * ReentrantLock、ReetrantReadWriteLock.ReadLock和ReetrantReadWriteLock.WriteLock，
 * 即重入锁、读锁和写锁。lock必须被显式地创建、锁定和释放，为了可以使用更多的功能，一般用ReentrantLock为其实例化。
 * 为了保证锁最终一定会被释放（可能会有异常发生），要把互斥区放在try语句块内，并在finally语句块中释放锁，尤其当有return语句时，
 * return语句必须放在try字句中，以确保unlock（）不会过早发生，从而将数据暴露给第二个任务。
 * 
 *     Lock lock = new ReentrantLock();//默认使用非公平锁，如果要使用公平锁，需要传入参数true  
    ........  
    lock.lock();  
    try {  
         //更新对象的状态  
        //捕获异常，必要时恢复到原来的不变约束  
       //如果有return语句，放在这里  
     finally {  
           lock.unlock();        //锁必须在finally块中释放  
 */
public class LockTest {

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
	public void testLock1() throws InterruptedException
	{
		init(2);
		SyncThread1 syncThread = new SyncThread1();
		new Thread(syncThread).start();
		new Thread(syncThread).start();
		countDownLatch.await();
	}

	/**
	 * 线程锁住不同对象实例非静态变量
	 */
	@Test
	public void testLock2() throws InterruptedException
	{
		init(2);
		new Thread(new SyncThread1()).start();
		new Thread(new SyncThread1()).start();
		countDownLatch.await();
	}

	/**
	 * 线程锁住不同对象实例静态变量
	 */
	@Test
	public void testLock3() throws InterruptedException
	{
		init(2);
		new Thread(new SyncThread2()).start();
		new Thread(new SyncThread2()).start();
		countDownLatch.await();
	}

	/**
	 * 读写锁
	 */
	@Test
	public void testLock4() throws InterruptedException
	{
		init(10);
		final Book book = new Book();
		for(int i=0; i<5; i++)
			new Thread(new Runnable() {

				@Override
				public void run() {
					book.write(new Random().nextInt());
					countDownLatch.countDown();
				}
			}).start();

		for(int i=0; i<5; i++)
			new Thread(new Runnable() {

				@Override
				public void run() {
					book.read();
					countDownLatch.countDown();
				}
			}).start();
		countDownLatch.await();
	}

	private static class SyncThread1 implements Runnable{
		
		private Lock lock = new ReentrantLock();
		
		private static int count = 0;
		
		@Override
		public void run() {
			lock.lock();
			try {
				for(int i=0; i<5; i++)
				{
					System.out.println(Thread.currentThread().getName() + ":" + count++);
					TimeUnit.MILLISECONDS.sleep(100);
				}
				LockTest.countDownLatch.countDown();
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				lock.unlock();
			}
		}
	}
	
	private static class SyncThread2 implements Runnable{
		
		private static Lock lock = new ReentrantLock();
		
		private static int count = 0;
		
		@Override
		public void run() {
			lock.lock();
			try {
				for(int i=0; i<5; i++)
				{
					System.out.println(Thread.currentThread().getName() + ":" + count++);
					TimeUnit.MILLISECONDS.sleep(100);
				}
				LockTest.countDownLatch.countDown();
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				lock.unlock();
			}
		}
	}
	
	private static class Book {
		
		private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
		
		private int data;
		
		public void write(int data)
		{
			lock.writeLock().lock();
			System.out.println(Thread.currentThread().getName() + ":准备写入");
			try {
				TimeUnit.MILLISECONDS.sleep(2000);
				this.data = data;
				System.out.println(Thread.currentThread().getName() + ":写入" + data);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}finally{
				lock.writeLock().unlock();
			}
		}
		
		public void read()
		{
			lock.readLock().lock();
			System.out.println(Thread.currentThread().getName() + ":准备读取");
			try {
				TimeUnit.MILLISECONDS.sleep(1000);
				System.out.println(Thread.currentThread().getName() + ":读取" + data);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}finally{
				lock.readLock().unlock();
			}
		}
	}
}
