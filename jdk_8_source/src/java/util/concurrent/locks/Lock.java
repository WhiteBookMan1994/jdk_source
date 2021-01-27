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
import java.util.concurrent.TimeUnit;

/**
 * {@code Lock} implementations provide more extensive locking
 * operations than can be obtained using {@code synchronized} methods
 * and statements.  They allow more flexible structuring, may have
 * quite different properties, and may support multiple associated
 * {@link Condition} objects.
 * Lock 实现提供了比使用 synchronized 方法和语句可获得的更广泛的锁定操作。
 * 此实现允许更灵活的结构，可以具有差别很大的属性，可以支持多个相关的 Condition 对象。
 *
 * <p>A lock is a tool for controlling access to a shared resource by
 * multiple threads. Commonly, a lock provides exclusive access to a
 * shared resource: only one thread at a time can acquire the lock and
 * all access to the shared resource requires that the lock be
 * acquired first. However, some locks may allow concurrent access to
 * a shared resource, such as the read lock of a {@link ReadWriteLock}.
 * 锁是控制多个线程对共享资源进行访问的工具。通常，锁提供了对共享资源的独占访问。
 * 一次只能有一个线程获得锁，对共享资源的所有访问都需要首先获得锁。
 * 不过，某些锁可能允许对共享资源并发访问，如 ReadWriteLock 的读取锁。
 *
 * <p>The use of {@code synchronized} methods or statements provides
 * access to the implicit monitor lock associated with every object, but
 * forces all lock acquisition and release to occur in a block-structured way:
 * when multiple locks are acquired they must be released in the opposite
 * order, and all locks must be released in the same lexical scope in which
 * they were acquired.
 * synchronized 方法或语句的使用提供了对与每个对象相关的隐式监视器锁的访问，
 * 但却强制所有锁获取和释放均要出现在一个块结构中：
 * 当获取了多个锁时，它们必须以相反的顺序释放，且必须在与所有锁被获取时相同的词法范围内释放所有锁。
 *
 * <p>While the scoping mechanism for {@code synchronized} methods
 * and statements makes it much easier to program with monitor locks,
 * and helps avoid many common programming errors involving locks,
 * there are occasions where you need to work with locks in a more
 * flexible way. For example, some algorithms for traversing
 * concurrently accessed data structures require the use of
 * &quot;hand-over-hand&quot; or &quot;chain locking&quot;: you
 * acquire the lock of node A, then node B, then release A and acquire
 * C, then release B and acquire D and so on.  Implementations of the
 * {@code Lock} interface enable the use of such techniques by
 * allowing a lock to be acquired and released in different scopes,
 * and allowing multiple locks to be acquired and released in any
 * order.
 * 虽然 synchronized 方法和语句的范围机制使得使用监视器锁编程方便了很多，而且还帮助避免了很多涉及到锁的常见编程错误，
 * 但有时也需要以更为灵活的方式使用锁。例如，某些遍历并发访问的数据结果的算法要求使用 "hand-over-hand" 或 "chain locking"：
 * 获取节点 A 的锁，然后再获取节点 B 的锁，然后释放 A 并获取 C，然后释放 B 并获取 D，依此类推。
 * Lock 接口的实现允许锁在不同的作用范围内获取和释放，并允许以任何顺序获取和释放多个锁，从而支持使用这种技术
 *
 * <p>With this increased flexibility comes additional
 * responsibility. The absence of block-structured locking removes the
 * automatic release of locks that occurs with {@code synchronized}
 * methods and statements. In most cases, the following idiom
 * should be used:
 * 随着灵活性的增加，也带来了更多的责任。不使用块结构锁就失去了使用 synchronized 方法和语句时会出现的锁自动释放功能。
 * 在大多数情况下，应该使用以下语句：
 *
 *  <pre> {@code
 * Lock l = ...;
 * l.lock();
 * try {
 *   // access the resource protected by this lock
 * } finally {
 *   l.unlock();
 * }}</pre>
 *
 * When locking and unlocking occur in different scopes, care must be
 * taken to ensure that all code that is executed while the lock is
 * held is protected by try-finally or try-catch to ensure that the
 * lock is released when necessary.
 * 锁定和取消锁定出现在不同作用范围中时，必须谨慎地确保保持锁定时所执行的所有代码用 try-finally 或 try-catch 加以保护，以确保在必要时释放锁。
 *
 * <p>{@code Lock} implementations provide additional functionality
 * over the use of {@code synchronized} methods and statements by
 * providing a non-blocking attempt to acquire a lock ({@link
 * #tryLock()}), an attempt to acquire the lock that can be
 * interrupted ({@link #lockInterruptibly}, and an attempt to acquire
 * the lock that can timeout ({@link #tryLock(long, TimeUnit)}).
 * Lock 实现提供了使用 synchronized 方法和语句所没有的其他功能，包括提供了一个非块结构的获取锁尝试 (tryLock())、
 * 一个获取可中断锁的尝试 (lockInterruptibly()) 和一个获取超时失效锁的尝试 (tryLock(long, TimeUnit))。
 *
 * <p>A {@code Lock} class can also provide behavior and semantics
 * that is quite different from that of the implicit monitor lock,
 * such as guaranteed ordering, non-reentrant usage, or deadlock
 * detection. If an implementation provides such specialized semantics
 * then the implementation must document those semantics.
 * Lock 类还可以提供与隐式监视器锁完全不同的行为和语义，如保证排序、非重入用法或死锁检测。
 * 如果某个实现提供了这样特殊的语义，则该实现必须对这些语义加以记录。
 *
 * <p>Note that {@code Lock} instances are just normal objects and can
 * themselves be used as the target in a {@code synchronized} statement.
 * Acquiring the
 * monitor lock of a {@code Lock} instance has no specified relationship
 * with invoking any of the {@link #lock} methods of that instance.
 * It is recommended that to avoid confusion you never use {@code Lock}
 * instances in this way, except within their own implementation.
 * 注意，Lock 实例只是普通的对象，其本身可以在 synchronized 语句中作为目标使用。
 * 获取 Lock 实例的监视器锁与调用该实例的任何 lock() 方法没有特别的关系。
 * 为了避免混淆，建议除了在其自身的实现中之外，决不要以这种方式使用 Lock 实例。
 *
 * <p>Except where noted, passing a {@code null} value for any
 * parameter will result in a {@link NullPointerException} being
 * thrown.
 * 除非另有说明，否则为任何参数传递 null 值都将导致抛出 NullPointerException。
 *
 * <h3>Memory Synchronization</h3>
 * <h3>内存同步</h3>
 *
 * <p>All {@code Lock} implementations <em>must</em> enforce the same
 * memory synchronization semantics as provided by the built-in monitor
 * lock, as described in
 * <a href="https://docs.oracle.com/javase/specs/jls/se7/html/jls-17.html#jls-17.4">
 * The Java Language Specification (17.4 Memory Model)</a>:
 * <ul>
 * <li>A successful {@code lock} operation has the same memory
 * synchronization effects as a successful <em>Lock</em> action.
 * <li>A successful {@code unlock} operation has the same
 * memory synchronization effects as a successful <em>Unlock</em> action.
 * </ul>
 * 所有 Lock 实现都必须 实施与内置监视器锁提供的相同内存同步语义，如 The Java Language Specification, Third Edition (17.4 Memory Model) 中所描述的:
 *
 * 成功的 lock 操作与成功的 Lock 操作具有同样的内存同步效应。
 * 成功的 unlock 操作与成功的 Unlock 操作具有同样的内存同步效应。
 *
 * Unsuccessful locking and unlocking operations, and reentrant
 * locking/unlocking operations, do not require any memory
 * synchronization effects.
 * 不成功的锁定与取消锁定操作以及重入锁定/取消锁定操作都不需要任何内存同步效果。
 *
 * <h3>Implementation Considerations</h3>
 * <h3>实现注意事项</h3>
 *
 * <p>The three forms of lock acquisition (interruptible,
 * non-interruptible, and timed) may differ in their performance
 * characteristics, ordering guarantees, or other implementation
 * qualities.  Further, the ability to interrupt the <em>ongoing</em>
 * acquisition of a lock may not be available in a given {@code Lock}
 * class.  Consequently, an implementation is not required to define
 * exactly the same guarantees or semantics for all three forms of
 * lock acquisition, nor is it required to support interruption of an
 * ongoing lock acquisition.  An implementation is required to clearly
 * document the semantics and guarantees provided by each of the
 * locking methods. It must also obey the interruption semantics as
 * defined in this interface, to the extent that interruption of lock
 * acquisition is supported: which is either totally, or only on
 * method entry.
 * 三种形式的锁获取（可中断、不可中断和定时）在其性能特征、排序保证或其他实现质量上可能会有所不同。
 * 而且，对于给定的 Lock 类，可能没有中断正在进行的 锁获取的能力。
 * 因此，并不要求实现为所有三种形式的锁获取定义相同的保证或语义，也不要求其支持中断正在进行的锁获取。
 * 实现必需清楚地对每个锁定方法所提供的语义和保证进行记录。
 * 还必须遵守此接口中定义的中断语义，以便为锁获取中断提供支持：完全支持中断，或仅在进入方法时支持中断。
 *
 * <p>As interruption generally implies cancellation, and checks for
 * interruption are often infrequent, an implementation can favor responding
 * to an interrupt over normal method return. This is true even if it can be
 * shown that the interrupt occurred after another action may have unblocked
 * the thread. An implementation should document this behavior.
 * 由于中断通常意味着取消，而通常又很少进行中断检查，因此，相对于普通方法返回而言，实现可能更喜欢响应某个中断。
 * 即使出现在另一个操作后的中断可能会释放线程锁时也是如此。实现应记录此行为。
 *
 * @see ReentrantLock
 * @see Condition
 * @see ReadWriteLock
 *
 * @since 1.5
 * @author Doug Lea
 */
public interface Lock {

    /**
     * Acquires the lock.
     * 获取锁
     * <p>If the lock is not available then the current thread becomes
     * disabled for thread scheduling purposes and lies dormant until the
     * lock has been acquired.
     * 如果锁不可用，出于线程调度目的，将禁用当前线程，并且在获得锁之前，该线程将一直处于休眠状态。
     * <p><b>Implementation Considerations</b>
     * <b>实现注意事项</b>
     * <p>A {@code Lock} implementation may be able to detect erroneous use
     * of the lock, such as an invocation that would cause deadlock, and
     * may throw an (unchecked) exception in such circumstances.  The
     * circumstances and the exception type must be documented by that
     * {@code Lock} implementation.
     * Lock 实现可能能够检测到锁的错误使用，比如会导致死锁的调用，在那种环境下还可能抛出一个 (unchecked) 异常。
     * Lock 实现必须对环境和异常类型进行记录。
     */
    void lock();

    /**
     * Acquires the lock unless the current thread is
     * {@linkplain Thread#interrupt interrupted}.
     * 如果当前线程未被 中断，则获取锁。
     *
     * <p>Acquires the lock if it is available and returns immediately.
     * 如果锁可用，则获取锁，并立即返回。
     *
     * <p>If the lock is not available then the current thread becomes
     * disabled for thread scheduling purposes and lies dormant until
     * one of two things happens:
     * 如果锁不可用，出于线程调度目的，将禁用当前线程，并且在发生以下两种情况之一以前，该线程将一直处于休眠状态：
     *
     * <ul>
     * <li>The lock is acquired by the current thread; or
     * <li>Some other thread {@linkplain Thread#interrupt interrupts} the
     * current thread, and interruption of lock acquisition is supported.
     * </ul>
     * 锁由当前线程获得；或者
     * 其他某个线程中断当前线程，并且支持对锁获取的中断。
     *
     * <p>If the current thread:
     * <ul>
     * <li>has its interrupted status set on entry to this method; or
     * <li>is {@linkplain Thread#interrupt interrupted} while acquiring the
     * lock, and interruption of lock acquisition is supported,
     * </ul>
     * then {@link InterruptedException} is thrown and the current thread's
     * interrupted status is cleared.
     * 如果当前线程：
     *
     * 在进入此方法时已经设置了该线程的中断状态；或者
     * 在获取锁时被中断，并且支持对锁获取的中断，
     * 则将抛出 InterruptedException，并清除当前线程的已中断状态。
     *
     * <p><b>Implementation Considerations</b>
     * 实现注意事项
     *
     * <p>The ability to interrupt a lock acquisition in some
     * implementations may not be possible, and if possible may be an
     * expensive operation.  The programmer should be aware that this
     * may be the case. An implementation should document when this is
     * the case.
     * 在某些实现中可能无法中断锁获取，即使可能，该操作的开销也很大。
     * 程序员应该知道可能会发生这种情况。在这种情况下，该实现应该对此进行记录。
     *
     * <p>An implementation can favor responding to an interrupt over
     * normal method return.
     * 相对于普通方法返回而言，实现可能更喜欢响应某个中断。
     *
     * <p>A {@code Lock} implementation may be able to detect
     * erroneous use of the lock, such as an invocation that would
     * cause deadlock, and may throw an (unchecked) exception in such
     * circumstances.  The circumstances and the exception type must
     * be documented by that {@code Lock} implementation.
     * Lock 实现可能可以检测锁的错误用法，例如，某个调用可能导致死锁，在特定的环境中可能抛出（未经检查的）异常。
     * 该 Lock 实现必须对环境和异常类型进行记录。
     *
     * @throws InterruptedException if the current thread is
     *         interrupted while acquiring the lock (and interruption
     *         of lock acquisition is supported)
     *         如果在获取锁时，当前线程被中断（并且支持对锁获取的中断）
     */
    void lockInterruptibly() throws InterruptedException;

    /**
     * Acquires the lock only if it is free at the time of invocation.
     * 仅在调用时锁为空闲状态才获取该锁。
     *
     * <p>Acquires the lock if it is available and returns immediately
     * with the value {@code true}.
     * If the lock is not available then this method will return
     * immediately with the value {@code false}.
     * 如果锁可用，则获取锁，并立即返回值 true。如果锁不可用，则此方法将立即返回值 false。
     * 此方法的典型使用语句如下:
     * <p>A typical usage idiom for this method would be:
     *  <pre> {@code
     * Lock lock = ...;
     * if (lock.tryLock()) {
     *   try {
     *     // manipulate protected state
     *   } finally {
     *     lock.unlock();
     *   }
     * } else {
     *   // perform alternative actions
     * }}</pre>
     *
     * This usage ensures that the lock is unlocked if it was acquired, and
     * doesn't try to unlock if the lock was not acquired.
     * 此用法可确保如果获取了锁，则会释放锁，如果未获取锁，则不会试图将其释放。
     *
     * @return {@code true} if the lock was acquired and
     *         {@code false} otherwise
     *         如果获取了锁，则返回 true；否则返回 false。
     */
    boolean tryLock();

    /**
     * Acquires the lock if it is free within the given waiting time and the
     * current thread has not been {@linkplain Thread#interrupt interrupted}.
     * 如果锁在给定的等待时间内空闲，并且当前线程未被 中断，则获取锁。
     *
     * <p>If the lock is available this method returns immediately
     * with the value {@code true}.
     * If the lock is not available then
     * the current thread becomes disabled for thread scheduling
     * purposes and lies dormant until one of three things happens:
     * <ul>
     * <li>The lock is acquired by the current thread; or
     * <li>Some other thread {@linkplain Thread#interrupt interrupts} the
     * current thread, and interruption of lock acquisition is supported; or
     * <li>The specified waiting time elapses
     * </ul>
     * 如果锁可用，则此方法将立即返回值 true。
     * 如果锁不可用，出于线程调度目的，将禁用当前线程，并且在发生以下三种情况之一前，该线程将一直处于休眠状态：
     * 锁由当前线程获得；或者
     * 其他某个线程中断当前线程，并且支持对锁获取的中断；或者
     * 已超过指定的等待时间
     *
     * <p>If the lock is acquired then the value {@code true} is returned.
     * 如果获得了锁，则返回值 true。
     *
     * <p>If the current thread:
     * <ul>
     * <li>has its interrupted status set on entry to this method; or
     * <li>is {@linkplain Thread#interrupt interrupted} while acquiring
     * the lock, and interruption of lock acquisition is supported,
     * </ul>
     * then {@link InterruptedException} is thrown and the current thread's
     * interrupted status is cleared.
     * 如果当前线程：
     *
     * 在进入此方法时已经设置了该线程的中断状态；或者
     * 在获取锁时被中断，并且支持对锁获取的中断，
     * 则将抛出 InterruptedException，并会清除当前线程的已中断状态。
     *
     * <p>If the specified waiting time elapses then the value {@code false}
     * is returned.
     * If the time is
     * less than or equal to zero, the method will not wait at all.
     * 如果超过了指定的等待时间，则将返回值 false。如果 time 小于等于 0，该方法将完全不等待。
     *
     * <p><b>Implementation Considerations</b>
     * 实现注意事项
     *
     * <p>The ability to interrupt a lock acquisition in some implementations
     * may not be possible, and if possible may
     * be an expensive operation.
     * The programmer should be aware that this may be the case. An
     * implementation should document when this is the case.
     * 在某些实现中可能无法中断锁获取，即使可能，该操作的开销也很大。
     * 程序员应该知道可能会发生这种情况。在这种情况下，该实现应该对此进行记录。
     *
     * <p>An implementation can favor responding to an interrupt over normal
     * method return, or reporting a timeout.
     * 相对于普通方法返回而言，实现可能更喜欢响应某个中断，或者报告出现超时情况。
     *
     * <p>A {@code Lock} implementation may be able to detect
     * erroneous use of the lock, such as an invocation that would cause
     * deadlock, and may throw an (unchecked) exception in such circumstances.
     * The circumstances and the exception type must be documented by that
     * {@code Lock} implementation.
     * Lock 实现可能可以检测锁的错误用法，
     * 例如，某个调用可能导致死锁，在特定的环境中可能抛出（未经检查的）异常。该 Lock 实现必须对环境和异常类型进行记录
     *
     * @param time the maximum time to wait for the lock 等待锁的最长时间
     * @param unit the time unit of the {@code time} argument 参数的时间单位
     * @return {@code true} if the lock was acquired and {@code false}
     *         if the waiting time elapsed before the lock was acquired
     *         如果获得了锁，则返回 true；如果在获取锁前超过了等待时间，则返回 false
     *
     * @throws InterruptedException if the current thread is interrupted
     *         while acquiring the lock (and interruption of lock
     *         acquisition is supported)
     *         如果在获取锁时，当前线程被中断（并且支持对锁获取的中断）
     */
    boolean tryLock(long time, TimeUnit unit) throws InterruptedException;

    /**
     * Releases the lock.
     * 释放锁。
     *
     * <p><b>Implementation Considerations</b>
     * 实现注意事项
     *
     * <p>A {@code Lock} implementation will usually impose
     * restrictions on which thread can release a lock (typically only the
     * holder of the lock can release it) and may throw
     * an (unchecked) exception if the restriction is violated.
     * Any restrictions and the exception
     * type must be documented by that {@code Lock} implementation.
     * Lock 实现通常对哪个线程可以释放锁施加了限制（通常只有锁的保持者可以释放它），
     * 如果违背了这个限制，可能会抛出（未经检查的）异常。该 Lock 实现必须对所有限制和异常类型进行记录。
     */
    void unlock();

    /**
     * Returns a new {@link Condition} instance that is bound to this
     * {@code Lock} instance.
     * 返回绑定到此 Lock 实例的新 Condition 实例。
     *
     * <p>Before waiting on the condition the lock must be held by the
     * current thread.
     * A call to {@link Condition#await()} will atomically release the lock
     * before waiting and re-acquire the lock before the wait returns.
     * 在等待条件前，锁必须由当前线程保持。
     * 调用 Condition.await() 将在等待前以原子方式释放锁，并在等待返回前重新获取锁。
     *
     * <p><b>Implementation Considerations</b>
     * 实现注意事项
     *
     * <p>The exact operation of the {@link Condition} instance depends on
     * the {@code Lock} implementation and must be documented by that
     * implementation.
     * Condition 实例的具体操作依赖于 Lock 实现，并且该实现必须对此加以记录。
     *
     * @return A new {@link Condition} instance for this {@code Lock} instance
     *         用于此 Lock 实例的新 Condition 实例
     * @throws UnsupportedOperationException if this {@code Lock}
     *         implementation does not support conditions
     *         如果此 Lock 实现不支持条件
     */
    Condition newCondition();
}
