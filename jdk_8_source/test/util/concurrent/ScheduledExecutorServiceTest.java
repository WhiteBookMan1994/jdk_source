package util.concurrent;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 定时调度任务线程池测试
 * @author dingchenchen
 * @since 2021/4/2
 */
public class ScheduledExecutorServiceTest {
    /**
     * RocketMQ broker 启动时 BrokerController 类用到了 ScheduledExecutorService
     * rocketmq release-4.7.1 版本
     * @param args
     */
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryImpl(
                "TestScheduledThread"));

        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                Thread thread = Thread.currentThread();
                System.out.println("我是间隔5s执行的任务，线程name："+ thread.getName() + ",执行时间戳：" + System.currentTimeMillis() / 1000);
            }
        }, 0L, 5, TimeUnit.SECONDS);

        ScheduledFuture scheduledFuture = scheduledExecutorService.schedule(new Callable() {
            @Override
            public Integer call() {
                Thread thread = Thread.currentThread();
                System.out.println("我是延迟10s执行的一次性任务，线程name："+ thread.getName() + ",执行时间戳：" + System.currentTimeMillis() / 1000);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return 250;
            }
        }, 10L,  TimeUnit.SECONDS);
        // scheduledFuture.get() 会阻塞调用方线程，这里是 main 线程
        System.out.println("scheduledFuture.get():" + scheduledFuture.get());
        System.out.println("hello world");
    }
}
class ThreadFactoryImpl implements ThreadFactory {
    private final AtomicLong threadIndex = new AtomicLong(0);
    private final String threadNamePrefix;
    private final boolean daemon;

    public ThreadFactoryImpl(final String threadNamePrefix) {
        this(threadNamePrefix, false);
    }

    public ThreadFactoryImpl(final String threadNamePrefix, boolean daemon) {
        this.threadNamePrefix = threadNamePrefix;
        this.daemon = daemon;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r, threadNamePrefix + this.threadIndex.incrementAndGet());
        thread.setDaemon(daemon);
        return thread;
    }
}
