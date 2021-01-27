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
        System.out.println("ll");
    }
}
