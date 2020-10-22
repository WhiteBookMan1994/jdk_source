package util.concurrent.completablefuture;

import java.util.concurrent.CompletableFuture;

/**
 * 使用CompletableFuture实现三个线程顺序执行
 * @author dingchenchen
 * @since 2020/10/22
 */
public class ThreadOrderExec {
    public static void main(String[] args) {
        Thread t1 = new Thread(new Worker());
        Thread t2 = new Thread(new Worker());
        Thread t3 = new Thread(new Worker());

        CompletableFuture.runAsync(()-> t1.start())
                .thenRun(()->t2.start())
                .thenRun(()->t3.start());
    }

    public static class Worker implements Runnable{

        @Override
        public void run() {
            System.out.println("thread start:" + Thread.currentThread().getName());
        }
    }
}
