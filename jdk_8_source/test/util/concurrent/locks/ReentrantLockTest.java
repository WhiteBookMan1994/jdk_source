package util.concurrent.locks;


import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 参考 Conditioon 接口文档中的示例，模拟一个阻塞队列
 *
 * @author dingchenchen
 * @since 2021/1/26
 */
public class ReentrantLockTest {

    private final ReentrantLock lock = new ReentrantLock();

    private final Condition notFull = lock.newCondition();

    private final Condition notEmpty = lock.newCondition();

    public int[] array = new int[5];

    /**
     * 队列中的元素个数
     */
    private int count = 0;

    /**
     * 下一个入队元素的索引位置
     */
    private int putPtr;

    /**
     * 下一个出队元素的索引位置
     */
    private int takePtr;

    public void put(int param) throws InterruptedException {
        lock.lock();
        try {
            while (count == array.length) {
                notFull.await();
            }
            array[putPtr] = param;
            if (++putPtr == array.length) {
                putPtr = 0;
            }
            count++;
            notEmpty.signal();
            //System.out.println(showArray());
        } finally {
            lock.unlock();
        }
    }

    public int take() throws InterruptedException {
        lock.lock();
        try {
            while (count == 0) {
                notEmpty.await();
            }
            int i = array[takePtr];
            array[takePtr] = 0;
            if (++takePtr == array.length) {
                takePtr = 0;
            }
            count--;
            notFull.signal();
            return i;
        } finally {
            lock.unlock();
        }
    }

    public String showArray(){
        StringBuilder sb = new StringBuilder();
        for (int i =0;i <array.length;i++) {
            sb.append(array[i]).append(',');
        }
        return sb.toString();
    }

    public static void main(String[] args) throws InterruptedException {
        ReentrantLockTest reentrantLockTest = new ReentrantLockTest();
        Thread put = new Thread(new PutTask(reentrantLockTest));
        Thread take = new Thread(new TakeTask(reentrantLockTest));
        put.start();
        Thread.sleep(2000);
        take.start();
    }
}

class PutTask implements Runnable {

    ReentrantLockTest lockTest;

    public PutTask(ReentrantLockTest lockTest) {
        this.lockTest = lockTest;
    }

    @Override
    public void run() {
        while(true){
            try {
                lockTest.put(new Random().nextInt(100));
                System.out.println("put线程放入元素后，数组数据：" + lockTest.showArray());
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class TakeTask implements Runnable{

    ReentrantLockTest lockTest;

    public TakeTask(ReentrantLockTest lockTest) {
        this.lockTest = lockTest;
    }

    @Override
    public void run() {
        while(true){
            try {
                lockTest.take();
                System.out.println("take线程放入元素后，数组数据：" + lockTest.showArray());
                Thread.sleep(8000);
            } catch(InterruptedException e){
                e.printStackTrace();
            }
        }
    }
}
