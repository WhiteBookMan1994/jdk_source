package util.concurrent;

import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author dingchenchen
 * @since 2020/10/13
 */
public class CompletableFutureTest {
    public static void main(String[] args) throws Exception {

        ExecutorService pool = Executors.newFixedThreadPool(2);

        Future<Integer> f1 = pool.submit(new MyCallable(100));
        Future<Integer> f2 = pool.submit(new MyCallable(200));
        System.out.println(new Date().toLocaleString()+"：执行Future.get造成主线程阻塞前");

        // V get()
        //f1.get()会阻塞当前主线程的运行，但不影响已经执行的其他线程
        // 例如，不影响已经执行的Future<Integer> f2 = pool.submit(new MyCallable(200));
        Integer i1 = f1.get();
        System.out.println(new Date().toLocaleString()+"：f1.get()已经执行了5s");

        Integer i2 = f2.get();
        System.out.println(new Date().toLocaleString()+"：f2.get()已经执行了10s");

        System.out.println(new Date().toLocaleString()+":"+i1);

        System.out.println(new Date().toLocaleString()+"：执行Future.get造成主线程阻塞后");

        System.out.println(new Date().toLocaleString()+":"+i2);

        // 结束
        //如果不调用线程池对象的shutdown方法，当线程池里面的任务执行完毕后主线程这个JVM不会退出。
        pool.shutdown();

    }

    static class MyCallable implements Callable<Integer> {

        private int number;

        public MyCallable(int number) {
            this.number = number;
        }

        @Override
        public Integer call() throws Exception {
            int sum = 0;
            for (int x = 1; x <= number; x++) {
                sum += x;
            }

            System.out.println(new Date().toLocaleString()+":"+Thread.currentThread().getName()+"执行完成等待5s或则10s");

            if(number == 100){
                Thread.sleep(5000);
            }else {
                Thread.sleep(10000);
            }

            return sum;
        }

    }

}
