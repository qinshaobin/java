package com.bin.java7.test.thread;

import org.junit.Test;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * http://www.jianshu.com/p/751d1eb7eb08
 * 
 *当你使用synchronized关键字的时候，是通过互斥器来保障线程安全以及对共享资源的同步访问。线程间也经常需要更进一步的协调执行，来完成复杂的并发任务，比如wait/notify模式就是一种在多线程环境下的协调执行机制。

通过API来获取和释放锁（使用互斥器）或者调用wait/notify等方法都是底层调用的方式。进一步来说，有必要为线程同步创建更高层次的抽象。通常用到的同步辅助类，就是对2个或多个线程间的同步活动机制做进一步封装，其内部原理是通过使用现有的底层API来实现复杂的线程间的协调。

有5种同步辅助类适用于常见的同步场景

1. Semaphore 信号量是一类经典的同步工具。信号量通常用来限制线程可以同时访问的（物理或逻辑）资源数量。

2.CountDownLatch 一种非常简单、但很常用的同步辅助类。其作用是在完成一组正在其他线程中执行的操作之前,允许一个或多个线程一直阻塞。

3.CyclicBarrier 一种可重置的多路同步点，在某些并发编程场景很有用。它允许一组线程互相等待，直到到达某个公共的屏障点 (common barrier point)。在涉及一组固定大小的线程的程序中，这些线程必须不时地互相等待，此时 CyclicBarrier 很有用。因为该 barrier在释放等待线程后可以重用，所以称它为循环的barrier。

4.Phaser 一种可重用的同步屏障，功能上类似于CyclicBarrier和CountDownLatch，但使用上更为灵活。非常适用于在多线程环境下同步协调分阶段计算任务（Fork/Join框架中的子任务之间需同步时，优先使用Phaser）

5.Exchanger 允许两个线程在某个汇合点交换对象，在某些管道设计时比较有用。Exchanger提供了一个同步点，在这个同步点，一对线程可以交换数据。每个线程通过exchange()方法的入口提供数据给他的伙伴线程，并接收他的伙伴线程提供的数据并返回。当两个线程通过Exchanger交换了对象，这个交换对于两个线程来说都是安全的。Exchanger可以认为是 SynchronousQueue 的双向形式，在运用到遗传算法和管道设计的应用中比较有用。
 */
public class ThreadSyncTest {

	//为了在junit中跑多线程,只有把主线程堵塞
	private static CountDownLatch countDownLatch;

	/**
	 * CountDownLatch：闭锁
	 * 在完成一组正在其他线程中执行的操作之前，它约束一个或多个线程一直等待。
	 * 可用于：子任务执行完成后再进行另一个操作。
	 */
	@Test
	public void testCountDownLatch() throws Exception
	{
		ExecutorService service = Executors.newFixedThreadPool(3);
		
		int count = 5;
		
		final CountDownLatch countDownLatch = new CountDownLatch(count);
		
		for(int i=0; i<count; i++)
		{
			service.execute(new Runnable() {
				
				@Override
				public void run() {
					try {
						TimeUnit.SECONDS.sleep(new Random().nextInt(5));
						System.out.println(Thread.currentThread().getName() + ":" + new Date());
						//子任务完成后，计数器减1
						countDownLatch.countDown();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
		
		//主线程一直被阻塞,直到count的计数器被设置为0
		//所有子任务结束后(计数器为0)，执行主任务
		//e.g.:如果for循环次数>计数器次数。则在计数器为0时通知主任务，然后再继续执行子任务
		countDownLatch.await();
		
		service.shutdown();
		System.out.println(Thread.currentThread().getName() + ":" + new Date());
	}

	/**
	 *CyclicBarrier初始化的时候，设置一个屏障数。线程调用await()方法的时候，这个线程就会被阻塞，
	 *当调用await()的线程数量到达屏障数的时候，主线程就会取消所有被阻塞线程的状态。

	 在CyclicBarrier的构造方法中，还可以设置一个barrierAction。在所有的屏障都到达之后，会启动一个线程来运行这里面的代码。
	 这里举一个例子：百米赛跑的运动员起跑前需要准备，所有选手准备完毕之后，才可以同时起跑。

	 相比CountDownLatch，CyclicBarrier是可以被循环使用的，而且遇到线程中断等情况时，还可以利用reset()方法，重置计数器，从这些方面来说，CyclicBarrier会比CountDownLatch更加灵活一些
	 */
	@Test
	public void testCyclicBarrier() throws Exception
	{
		int count = 5;
		
		ExecutorService service = Executors.newFixedThreadPool(count + 1);

		countDownLatch = new CountDownLatch(count);
		final CyclicBarrier cyclicBarrier = new CyclicBarrier(count);
		
		for(int i=0; i<count; i++)
			service.execute(new Runner(cyclicBarrier));

		countDownLatch.await();

		//e.g.:线程次数量必须>=子任务数量。因为子任务一直被阻塞，不会释放线程
		service.shutdown();
	}
	
	private static class Runner implements Runnable{
		
		private CyclicBarrier cyclicBarrier;
		
		public Runner(CyclicBarrier cyclicBarrier) {
			this.cyclicBarrier = cyclicBarrier;
		}

		@Override
		public void run() {
			try {
				int time = new Random().nextInt(5);
				TimeUnit.SECONDS.sleep(time);
				System.out.printf("%s号选手准备完毕,准备时间%d\n", Thread.currentThread().getName(), time);
				cyclicBarrier.await();
				ThreadSyncTest.countDownLatch.countDown();
				System.out.printf("%s号选手于%s时起跑!\n", Thread.currentThread().getName(), new Date());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
