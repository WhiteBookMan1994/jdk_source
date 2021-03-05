package util.concurrent;

import org.junit.Test;

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
}
