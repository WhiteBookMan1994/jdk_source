package util.concurrent.locks;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author dingchenchen
 * @since 2021/1/26
 */
public class ReentrantLockTest {

    private final ReentrantLock lock = new ReentrantLock();

    public void test() {
        lock.lock();
        try {

            // do somethings
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        try{
            int i = 1 /0;
        }finally {
            System.out.println("ll");
        }
    }
}
