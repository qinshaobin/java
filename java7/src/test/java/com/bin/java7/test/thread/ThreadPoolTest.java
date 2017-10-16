package com.bin.java7.test.thread;

import org.junit.Test;

import java.util.concurrent.*;

/**
 * Java通过Executors提供四种线程池，分别为：
newCachedThreadPool创建一个可缓存线程池，如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程。
	线程池为无限大，当执行第二个任务时第一个任务已经完成，会复用执行第一个任务的线程，而不用每次新建线程。
newFixedThreadPool 创建一个定长线程池，可控制线程最大并发数，超出的线程会在队列中等待。
newScheduledThreadPool 创建一个定长线程池，支持定时及周期性任务执行。
newSingleThreadExecutor 创建一个单线程化的线程池，它只会用唯一的工作线程来执行任务，保证所有任务按照指定顺序(FIFO, LIFO, 优先级)执行。
 *
 */
public class ThreadPoolTest {

	//为了在junit中跑多线程,只有把主线程堵塞
	private static CountDownLatch countDownLatch;

	public void init(int count)
	{
		countDownLatch = new CountDownLatch(count);
	}

	/**
	 * 线程池为无限大，当执行第二个任务时第一个任务已经完成，会复用执行第一个任务的线程，而不用每次新建线程。
	 */
	@Test
	public void testNewCachedThreadPool() throws Exception
	{
		init(10);
		ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
		for(int i=0; i<10; i++)
		{
			final int index = i;
			cachedThreadPool.execute(new Runnable() {
				
				@Override
				public void run() {
					try {
						System.out.println(Thread.currentThread().getName() + ":" + index);
						TimeUnit.SECONDS.sleep(index);
						countDownLatch.countDown();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
			TimeUnit.SECONDS.sleep(1);
		}
		countDownLatch.await();
		cachedThreadPool.shutdown();
	}
	
	/**
	 * 创建一个定长线程池，可控制线程最大并发数，超出的线程会在队列中等待。
	 */
	@Test
	public void testNewFixedThreadPool() throws Exception
	{
		init(10);
		ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(3);
		for(int i=0; i<10; i++)
		{
			final int index = i;
			newFixedThreadPool.execute(new Runnable() {
				
				@Override
				public void run() {
					try {
						System.out.println(Thread.currentThread().getName() + ":" + index);
						TimeUnit.SECONDS.sleep(2);
						countDownLatch.countDown();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
		}
		countDownLatch.await();
		newFixedThreadPool.shutdown();
	}
	
	/**
	 * 创建一个定长线程池，支持定时及周期性任务执行。
	 */
	@Test
	public void testNewScheduledThreadPool() throws Exception
	{
		init(10);
		ScheduledExecutorService newScheduledThreadPool1 = Executors.newScheduledThreadPool(3);
		//延迟执行
		newScheduledThreadPool1.schedule(new Runnable() {
			
			@Override
			public void run() {
				System.out.println("延迟3秒执行");
				countDownLatch.countDown();
			}
		}, 3, TimeUnit.SECONDS);
		newScheduledThreadPool1.shutdown();
		
		//延迟后周期执行
		ScheduledExecutorService newScheduledThreadPool2 = Executors.newScheduledThreadPool(3);
		//延迟执行
		newScheduledThreadPool2.scheduleAtFixedRate(new Runnable() {
			
			@Override
			public void run() {
				System.out.println("延迟5秒，然后每3秒执行一次");
				countDownLatch.countDown();
			}
		}, 5, 3, TimeUnit.SECONDS);
		countDownLatch.await();
		newScheduledThreadPool2.shutdown();
	}
	
	/**
	 * 创建一个单线程化的线程池，它只会用唯一的工作线程来执行任务，保证所有任务按照指定顺序(FIFO, LIFO, 优先级)执行
	 */
	@Test
	public void testNewSingleThreadExecutor() throws Exception
	{
		init(10);
		ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
		for(int i=0; i<10; i++)
		{
			final int index = i;
			singleThreadExecutor.execute(new Runnable() {
				
				@Override
				public void run() {
					System.out.println(Thread.currentThread().getName() + ":" + index);
					try {
						TimeUnit.SECONDS.sleep(2);
						countDownLatch.countDown();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
		}
		countDownLatch.await();
		singleThreadExecutor.shutdown();
	}
}
