package util.concurrent.locks;

import java.util.concurrent.locks.LockSupport;

/**
 * @author dingchenchen
 * @since 2021/3/22
 */
public class LockSupportTest {

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(new MyThread());
        t1.start();
        Thread.sleep(2000);
        t1.interrupt();
        /** t1.interrupt() 和 LockSupport.unpark(t1) 都可以恢复线程执行*/
        //LockSupport.unpark(t1);
    }

    public static class MyThread implements Runnable{

        int i = 0;

        @Override
        public void run() {
            LockSupport.park();
            while(true){
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println("被中断了");
                }
                System.out.println("继续执行");
                System.out.println("i=" + i++);
            }
        }
    }
}
