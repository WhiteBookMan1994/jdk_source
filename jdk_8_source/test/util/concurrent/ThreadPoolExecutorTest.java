package util.concurrent;

import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author dingchenchen
 * @since 2021/3/2
 */
public class ThreadPoolExecutorTest {

    public static void main(String[] args) {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(1,1,0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(17), new ThreadPoolExecutor.CallerRunsPolicy());
        int j = 0;
        // 队列放满，同时也达到了maxPoolSize，则后续放入的都由调用线程池的线程来执行（本例子中是main线程）
        for (int i = 0; i <=20; i++) {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("ThreadName:"+Thread.currentThread().getName());
                }
            });
        }
    }

    @Test
    public void testCallable(){
        ThreadPoolExecutor executor = new ThreadPoolExecutor(1,1,0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1), new ThreadPoolExecutor.CallerRunsPolicy());
        int j = 0;
        // 队列放满，同时也达到了maxPoolSize，则后续放入的都由调用线程池的线程来执行（本例子中是main线程）
        // ThreadPoolExecutor.CallerRunsPolicy() 这种策略不会抛异常，不断扔进去的任务会等待线程池中的线程或者调用者线程执行任务
        for (int i = 0; i <=20; i++) {
            executor.submit(new Callable<Integer>() {
                @Override
                public Integer call() {
                    try {
                        Thread.sleep(2000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("ThreadName:"+Thread.currentThread().getName());
                    return j;
                }
            });
        }
    }

    @Test
    public void testShutDown(){
        ThreadPoolExecutor executor = new ThreadPoolExecutor(2,4,1000,TimeUnit.MILLISECONDS,new ArrayBlockingQueue<>(10));
        executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                    System.out.println("hello world");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                    System.out.println("hello kitty");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        executor.shutdown();
        //shutdown之后再提交任务抛出异常java.util.concurrent.RejectedExecutionException
        //并且不会等待已提交任务执行完成
        executor.submit(new Runnable() {
            @Override
            public void run() {
                System.out.println("hello man");
            }
        });
    }

    @Test
    public void testAwaitTermination() throws InterruptedException {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 4, 1000, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(10));
        executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                    System.out.println("hello world");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                    System.out.println("hello kitty");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        /*
         * 如果 awaitTermination 设置的超时时候过短，也可能已提交的任务没有执行完毕就关闭线程池了
         */
        executor.awaitTermination(8, TimeUnit.SECONDS);

        executor.submit(new Runnable() {
            @Override
            public void run() {
                System.out.println("hello man");
            }
        });
    }
}
