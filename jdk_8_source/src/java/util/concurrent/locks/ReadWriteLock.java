/*
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

/*
 *
 *
 *
 *
 *
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/publicdomain/zero/1.0/
 */

package java.util.concurrent.locks;

/**
 * A {@code ReadWriteLock} maintains a pair of associated {@link
 * Lock locks}, one for read-only operations and one for writing.
 * The {@link #readLock read lock} may be held simultaneously by
 * multiple reader threads, so long as there are no writers.  The
 * {@link #writeLock write lock} is exclusive.
 *
 * ReadWriteLock 维护了一对相关的锁，一个用于只读操作，另一个用于写入操作。
 * 只要没有 writer，读锁可以由多个 reader 线程同时保持。写锁是独占的。
 *
 * <p>All {@code ReadWriteLock} implementations must guarantee that
 * the memory synchronization effects of {@code writeLock} operations
 * (as specified in the {@link Lock} interface) also hold with respect
 * to the associated {@code readLock}. That is, a thread successfully
 * acquiring the read lock will see all updates made upon previous
 * release of the write lock.
 *
 * 所有 ReadWriteLock 实现都必须保证 writeLock 操作的内存同步效果也要保持与相关 readLock 的联系。
 * 换句话说，一个获得了读锁的线程必须能看到前一个释放的写锁所更新的内容。
 *
 * <p>A read-write lock allows for a greater level of concurrency in
 * accessing shared data than that permitted by a mutual exclusion lock.
 * It exploits the fact that while only a single thread at a time (a
 * <em>writer</em> thread) can modify the shared data, in many cases any
 * number of threads can concurrently read the data (hence <em>reader</em>
 * threads).
 * In theory, the increase in concurrency permitted by the use of a read-write
 * lock will lead to performance improvements over the use of a mutual
 * exclusion lock. In practice this increase in concurrency will only be fully
 * realized on a multi-processor, and then only if the access patterns for
 * the shared data are suitable.
 *
 * 与互斥锁相比，读-写锁允许对共享数据进行更高级别的并发访问。
 * 虽然一次只有一个线程（writer 线程）可以修改共享数据，但在许多情况下，任何数量的线程可以同时读取共享数据（reader 线程）。
 * 读-写锁利用了这一点。从理论上讲，与互斥锁相比，使用读-写锁所允许的并发性增强将带来更大的性能提高。
 * 在实践中，只有在多处理器上并且只在访问模式适用于共享数据时，才能完全实现并发性增强。
 *
 * <p>Whether or not a read-write lock will improve performance over the use
 * of a mutual exclusion lock depends on the frequency that the data is
 * read compared to being modified, the duration of the read and write
 * operations, and the contention for the data - that is, the number of
 * threads that will try to read or write the data at the same time.
 * For example, a collection that is initially populated with data and
 * thereafter infrequently modified, while being frequently searched
 * (such as a directory of some kind) is an ideal candidate for the use of
 * a read-write lock. However, if updates become frequent then the data
 * spends most of its time being exclusively locked and there is little, if any
 * increase in concurrency. Further, if the read operations are too short
 * the overhead of the read-write lock implementation (which is inherently
 * more complex than a mutual exclusion lock) can dominate the execution
 * cost, particularly as many read-write lock implementations still serialize
 * all threads through a small section of code. Ultimately, only profiling
 * and measurement will establish whether the use of a read-write lock is
 * suitable for your application.
 *
 * 与互斥锁相比，使用读-写锁能否提升性能则取决于读写操作期间读取数据相对于修改数据的频率，以及数据的争用——即在同一时间试图对该数据执行读取或写入操作的线程数。
 * 例如，某个最初用数据填充并且之后不经常对其进行修改的 collection，因为经常对其进行搜索（比如搜索某种目录），所以这样的 collection 是使用读-写锁的理想候选者。
 * 但是，如果数据更新变得频繁，数据在大部分时间都被独占锁，这时，就算存在并发性增强，也是微不足道的。
 * 更进一步地说，如果读取操作所用时间太短，则读-写锁实现（它本身就比互斥锁复杂）的开销将成为主要的执行成本，在许多读-写锁实现仍然通过一小段代码将所有线程序列化时更是如此。
 * 最终，只有通过分析和测量，才能确定应用程序是否适合使用读-写锁。
 *
 * <p>Although the basic operation of a read-write lock is straight-forward,
 * there are many policy decisions that an implementation must make, which
 * may affect the effectiveness of the read-write lock in a given application.
 * Examples of these policies include:
 * 尽管读-写锁的基本操作是直截了当的，但实现仍然必须作出许多决策，这些决策可能会影响给定应用程序中读-写锁的效果。
 * 这些策略的例子包括：
 * <ul>
 * <li>Determining whether to grant the read lock or the write lock, when
 * both readers and writers are waiting, at the time that a writer releases
 * the write lock. Writer preference is common, as writes are expected to be
 * short and infrequent. Reader preference is less common as it can lead to
 * lengthy delays for a write if the readers are frequent and long-lived as
 * expected. Fair, or &quot;in-order&quot; implementations are also possible.
 * 在 writer 释放写入锁时，reader 和 writer 都处于等待状态，在这时要确定是授予读取锁还是授予写入锁。Writer 优先比较普遍，
 * 因为预期写入所需的时间较短并且不那么频繁。Reader 优先不太普遍，因为如果 reader 正如预期的那样频繁和持久，
 * 那么它将导致对于写入操作来说较长的时延。公平或者“按次序”实现也是有可能的
 *
 * <li>Determining whether readers that request the read lock while a
 * reader is active and a writer is waiting, are granted the read lock.
 * Preference to the reader can delay the writer indefinitely, while
 * preference to the writer can reduce the potential for concurrency.
 * 在 reader 处于活动状态而 writer 处于等待状态时，确定是否向请求读取锁的 reader 授予读取锁。
 * Reader 优先会无限期地延迟 writer，而 writer 优先会减少可能的并发。
 *
 * <li>Determining whether the locks are reentrant: can a thread with the
 * write lock reacquire it? Can it acquire a read lock while holding the
 * write lock? Is the read lock itself reentrant?
 * 确定是否是可重入锁：可以使用带有写入锁的线程重新获取它吗？可以在保持写入锁的同时获取读取锁吗？读锁本身是否可重入？
 *
 * <li>Can the write lock be downgraded to a read lock without allowing
 * an intervening writer? Can a read lock be upgraded to a write lock,
 * in preference to other waiting readers or writers?
 * 可以将写入锁在不允许其他 writer 干涉的情况下降级为读取锁吗？可以优先于其他等待的 reader 或 writer 将读取锁升级为写入锁吗？
 *
 * </ul>
 * You should consider all of these things when evaluating the suitability
 * of a given implementation for your application.
 * 当评估给定实现是否适合您的应用程序时，应该考虑所有这些情况。
 *
 * @see ReentrantReadWriteLock
 * @see Lock
 * @see ReentrantLock
 *
 * @since 1.5
 * @author Doug Lea
 */
public interface ReadWriteLock {
    /**
     * Returns the lock used for reading.
     * 返回读锁
     * @return the lock used for reading
     */
    Lock readLock();

    /**
     * Returns the lock used for writing.
     * 返回写锁
     * @return the lock used for writing
     */
    Lock writeLock();
}
