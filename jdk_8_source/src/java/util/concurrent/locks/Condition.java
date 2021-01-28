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
import java.util.Date;

/**
 * {@code Condition} factors out the {@code Object} monitor
 * methods ({@link Object#wait() wait}, {@link Object#notify notify}
 * and {@link Object#notifyAll notifyAll}) into distinct objects to
 * give the effect of having multiple wait-sets per object, by
 * combining them with the use of arbitrary {@link Lock} implementations.
 * Where a {@code Lock} replaces the use of {@code synchronized} methods
 * and statements, a {@code Condition} replaces the use of the Object
 * monitor methods.
 *
 * Condition 将 Object 监视器方法（wait、notify 和 notifyAll）分解成截然不同的对象，
 * 以便通过将这些对象与任意 Lock 实现组合使用，为每个对象提供多个等待 set（wait-set）。
 * 其中，Lock 替代了 synchronized 方法和语句的使用，Condition 替代了 Object 监视器方法的使用。
 *
 * <p>Conditions (also known as <em>condition queues</em> or
 * <em>condition variables</em>) provide a means for one thread to
 * suspend execution (to &quot;wait&quot;) until notified by another
 * thread that some state condition may now be true.  Because access
 * to this shared state information occurs in different threads, it
 * must be protected, so a lock of some form is associated with the
 * condition. The key property that waiting for a condition provides
 * is that it <em>atomically</em> releases the associated lock and
 * suspends the current thread, just like {@code Object.wait}.
 *
 * 条件（也称为条件队列 或条件变量）为线程提供了一个含义，
 * 以便在某个状态条件现在可能为 true 的另一个线程通知它之前，一直挂起该线程（即让其“等待”）。
 * 因为访问此共享状态信息发生在不同的线程中，所以它必须受保护，因此要将某种形式的锁与该条件相关联。
 * 等待提供一个条件的主要属性是：以原子方式 释放相关的锁，并挂起当前线程，就像 Object.wait 做的那样。
 *
 * <p>A {@code Condition} instance is intrinsically bound to a lock.
 * To obtain a {@code Condition} instance for a particular {@link Lock}
 * instance use its {@link Lock#newCondition newCondition()} method.
 *
 * Condition 实例实质上被绑定到一个锁上。要为特定 Lock 实例获得 Condition 实例，请使用其 newCondition() 方法。
 *
 * <p>As an example, suppose we have a bounded buffer which supports
 * {@code put} and {@code take} methods.  If a
 * {@code take} is attempted on an empty buffer, then the thread will block
 * until an item becomes available; if a {@code put} is attempted on a
 * full buffer, then the thread will block until a space becomes available.
 * We would like to keep waiting {@code put} threads and {@code take}
 * threads in separate wait-sets so that we can use the optimization of
 * only notifying a single thread at a time when items or spaces become
 * available in the buffer. This can be achieved using two
 * {@link Condition} instances.
 *
 * 作为一个示例，假定有一个绑定的缓冲区，它支持 put 和 take 方法。如果试图在空的缓冲区上执行 take 操作，
 * 则在某一个项变得可用之前，线程将一直阻塞；
 * 如果试图在满的缓冲区上执行 put 操作，则在有空间变得可用之前，线程将一直阻塞。
 * 我们喜欢在单独的等待 set 中保存 put 线程和 take 线程，这样就可以在缓冲区中的项或空间变得可用时利用最佳规划，
 * 一次只通知一个线程。可以使用两个 Condition 实例来做到这一点。
 *
 * <pre>
 * class BoundedBuffer {
 *   <b>final Lock lock = new ReentrantLock();</b>
 *   final Condition notFull  = <b>lock.newCondition(); </b>
 *   final Condition notEmpty = <b>lock.newCondition(); </b>
 *
 *   final Object[] items = new Object[100];
 *   int putptr, takeptr, count;
 *
 *   public void put(Object x) throws InterruptedException {
 *     <b>lock.lock();
 *     try {</b>
 *       while (count == items.length)
 *         <b>notFull.await();</b>
 *       items[putptr] = x;
 *       if (++putptr == items.length) putptr = 0;
 *       ++count;
 *       <b>notEmpty.signal();</b>
 *     <b>} finally {
 *       lock.unlock();
 *     }</b>
 *   }
 *
 *   public Object take() throws InterruptedException {
 *     <b>lock.lock();
 *     try {</b>
 *       while (count == 0)
 *         <b>notEmpty.await();</b>
 *       Object x = items[takeptr];
 *       if (++takeptr == items.length) takeptr = 0;
 *       --count;
 *       <b>notFull.signal();</b>
 *       return x;
 *     <b>} finally {
 *       lock.unlock();
 *     }</b>
 *   }
 * }
 * </pre>
 *
 * (The {@link java.util.concurrent.ArrayBlockingQueue} class provides
 * this functionality, so there is no reason to implement this
 * sample usage class.)
 *
 * （ ArrayBlockingQueue 类提供了这项功能，因此没有理由去实现这个示例类。）
 *
 * <p>A {@code Condition} implementation can provide behavior and semantics
 * that is
 * different from that of the {@code Object} monitor methods, such as
 * guaranteed ordering for notifications, or not requiring a lock to be held
 * when performing notifications.
 * If an implementation provides such specialized semantics then the
 * implementation must document those semantics.
 *
 * Condition 实现可以提供不同于 Object 监视器方法的行为和语义，
 * 比如受保证的通知排序，或者在执行通知时不需要保持一个锁。
 * 如果某个实现提供了这样特殊的语义，则该实现必须记录这些语义。
 *
 * <p>Note that {@code Condition} instances are just normal objects and can
 * themselves be used as the target in a {@code synchronized} statement,
 * and can have their own monitor {@link Object#wait wait} and
 * {@link Object#notify notification} methods invoked.
 * Acquiring the monitor lock of a {@code Condition} instance, or using its
 * monitor methods, has no specified relationship with acquiring the
 * {@link Lock} associated with that {@code Condition} or the use of its
 * {@linkplain #await waiting} and {@linkplain #signal signalling} methods.
 * It is recommended that to avoid confusion you never use {@code Condition}
 * instances in this way, except perhaps within their own implementation.
 *
 * 注意，Condition 实例只是一些普通的对象，它们自身可以用作 synchronized 语句中的目标，
 * 并且可以调用自己的 wait 和 notify 监视器方法。
 * 获取 Condition 实例的监视器锁或者使用其监视器方法，与获取和该 Condition 相关的 Lock 或使用其 waiting 和 signalling 方法没有什么特定的关系。
 * 为了避免混淆，建议除了在其自身的实现中之外，切勿以这种方式使用 Condition 实例。
 *
 * <p>Except where noted, passing a {@code null} value for any parameter
 * will result in a {@link NullPointerException} being thrown.
 *
 * 除非另行说明，否则为任何参数传递 null 值将导致抛出 NullPointerException。
 *
 * <h3>Implementation Considerations</h3>
 *
 * <h3>实现注意事项</h3>
 *
 * <p>When waiting upon a {@code Condition}, a &quot;<em>spurious
 * wakeup</em>&quot; is permitted to occur, in
 * general, as a concession to the underlying platform semantics.
 * This has little practical impact on most application programs as a
 * {@code Condition} should always be waited upon in a loop, testing
 * the state predicate that is being waited for.  An implementation is
 * free to remove the possibility of spurious wakeups but it is
 * recommended that applications programmers always assume that they can
 * occur and so always wait in a loop.
 *
 * 在等待 Condition 时，允许发生“虚假唤醒”，这通常作为对基础平台语义的让步。
 * 对于大多数应用程序，这带来的实际影响很小，因为 Condition 应该总是在一个循环中被等待，并测试正被等待的状态声明。
 * 某个实现可以随意移除可能的虚假唤醒，但建议应用程序程序员总是假定这些虚假唤醒可能发生，因此总是在一个循环中等待。
 *
 * <p>The three forms of condition waiting
 * (interruptible, non-interruptible, and timed) may differ in their ease of
 * implementation on some platforms and in their performance characteristics.
 * In particular, it may be difficult to provide these features and maintain
 * specific semantics such as ordering guarantees.
 * Further, the ability to interrupt the actual suspension of the thread may
 * not always be feasible to implement on all platforms.
 *
 * 三种形式的条件等待（可中断、不可中断和超时）在一些平台上的实现以及它们的性能特征可能会有所不同。
 * 尤其是它可能很难提供这些特性和维护特定语义，比如排序保证。
 * 更进一步地说，中断线程实际挂起的能力在所有平台上并不是总是可行的。
 *
 * <p>Consequently, an implementation is not required to define exactly the
 * same guarantees or semantics for all three forms of waiting, nor is it
 * required to support interruption of the actual suspension of the thread.
 *
 * 因此，并不要求某个实现为所有三种形式的等待定义完全相同的保证或语义，也不要求其支持中断线程的实际挂起。
 *
 * <p>An implementation is required to
 * clearly document the semantics and guarantees provided by each of the
 * waiting methods, and when an implementation does support interruption of
 * thread suspension then it must obey the interruption semantics as defined
 * in this interface.
 *
 * 要求实现清楚地记录每个等待方法提供的语义和保证，在某个实现不支持中断线程的挂起时，它必须遵从此接口中定义的中断语义。
 *
 * <p>As interruption generally implies cancellation, and checks for
 * interruption are often infrequent, an implementation can favor responding
 * to an interrupt over normal method return. This is true even if it can be
 * shown that the interrupt occurred after another action that may have
 * unblocked the thread. An implementation should document this behavior.
 *
 * 由于中断通常意味着取消，而又通常很少进行中断检查，因此实现可以先于普通方法的返回来对中断进行响应。
 * 即使出现在另一个操作后的中断可能会释放线程锁时也是如此。实现应记录此行为。
 *
 * @since 1.5
 * @author Doug Lea
 */
public interface Condition {

    /**
     * Causes the current thread to wait until it is signalled or
     * {@linkplain Thread#interrupt interrupted}.
     *
     * 造成当前线程在接到信号或被 中断之前一直处于等待状态。
     *
     * <p>The lock associated with this {@code Condition} is atomically
     * released and the current thread becomes disabled for thread scheduling
     * purposes and lies dormant until <em>one</em> of four things happens:
     * <ul>
     * <li>Some other thread invokes the {@link #signal} method for this
     * {@code Condition} and the current thread happens to be chosen as the
     * thread to be awakened; or
     * <li>Some other thread invokes the {@link #signalAll} method for this
     * {@code Condition}; or
     * <li>Some other thread {@linkplain Thread#interrupt interrupts} the
     * current thread, and interruption of thread suspension is supported; or
     * <li>A &quot;<em>spurious wakeup</em>&quot; occurs.
     * </ul>
     *
     * <p>In all cases, before this method can return the current thread must
     * re-acquire the lock associated with this condition. When the
     * thread returns it is <em>guaranteed</em> to hold this lock.
     *
     * 与此 Condition 相关的锁以原子方式释放，并且出于线程调度的目的，将禁用当前线程，且在发生以下四种情况之一 以前，当前线程将一直处于休眠状态：
     *
     * 其他某个线程调用此 Condition 的 signal() 方法，并且碰巧将当前线程选为被唤醒的线程；或者
     * 其他某个线程调用此 Condition 的 signalAll() 方法；或者
     * 其他某个线程中断当前线程，且支持中断线程的挂起；或者
     * 发生“虚假唤醒”
     * 在所有情况下，在此方法可以返回当前线程之前，都必须重新获取与此条件有关的锁。在线程返回时，可以保证 它保持此锁。
     *
     * <p>If the current thread:
     * <ul>
     * <li>has its interrupted status set on entry to this method; or
     * <li>is {@linkplain Thread#interrupt interrupted} while waiting
     * and interruption of thread suspension is supported,
     * </ul>
     * then {@link InterruptedException} is thrown and the current thread's
     * interrupted status is cleared. It is not specified, in the first
     * case, whether or not the test for interruption occurs before the lock
     * is released.
     *
     * 如果当前线程：
     *
     * 在进入此方法时已经设置了该线程的中断状态；或者
     * 在支持等待和中断线程挂起时，线程被中断，
     * 则抛出 InterruptedException，并清除当前线程的中断状态。在第一种情况下，没有指定是否在释放锁之前发生中断测试。
     *
     * <p><b>Implementation Considerations</b>
     *
     * ><b>实现注意事项</b>
     *
     * <p>The current thread is assumed to hold the lock associated with this
     * {@code Condition} when this method is called.
     * It is up to the implementation to determine if this is
     * the case and if not, how to respond. Typically, an exception will be
     * thrown (such as {@link IllegalMonitorStateException}) and the
     * implementation must document that fact.
     *
     * 假定调用此方法时，当前线程保持了与此 Condition 有关联的锁。
     * 这取决于确定是否为这种情况以及不是时，如何对此作出响应的实现。
     * 通常，将抛出一个异常（比如 IllegalMonitorStateException）并且该实现必须对此进行记录。
     *
     * <p>An implementation can favor responding to an interrupt over normal
     * method return in response to a signal. In that case the implementation
     * must ensure that the signal is redirected to another waiting thread, if
     * there is one.
     *
     * 与响应某个信号而返回的普通方法相比，实现可能更喜欢响应某个中断。
     * 在这种情况下，实现必须确保信号被重定向到另一个等待线程（如果有的话）。
     *
     * @throws InterruptedException if the current thread is interrupted
     *         (and interruption of thread suspension is supported)
     *          如果当前线程被中断（并且支持中断线程挂起）
     */
    void await() throws InterruptedException;

    /**
     * Causes the current thread to wait until it is signalled.
     *
     * 造成当前线程在接到信号之前一直处于等待状态。
     *
     * <p>The lock associated with this condition is atomically
     * released and the current thread becomes disabled for thread scheduling
     * purposes and lies dormant until <em>one</em> of three things happens:
     * <ul>
     * <li>Some other thread invokes the {@link #signal} method for this
     * {@code Condition} and the current thread happens to be chosen as the
     * thread to be awakened; or
     * <li>Some other thread invokes the {@link #signalAll} method for this
     * {@code Condition}; or
     * <li>A &quot;<em>spurious wakeup</em>&quot; occurs.
     * </ul>
     *
     * <p>In all cases, before this method can return the current thread must
     * re-acquire the lock associated with this condition. When the
     * thread returns it is <em>guaranteed</em> to hold this lock.
     *
     * 与此条件相关的锁以原子方式释放，并且出于线程调度的目的，将禁用当前线程，且在发生以下三种情况之一 以前，当前线程将一直处于休眠状态：
     *
     * 其他某个线程调用此 Condition 的 signal() 方法，并且碰巧将当前线程选为被唤醒的线程；或者
     * 其他某个线程调用此 Condition 的 signalAll() 方法；或者
     * 发生“虚假唤醒”
     * 在所有情况下，在此方法可以返回当前线程之前，都必须重新获取与此条件有关的锁。在线程返回时，可以保证 它保持此锁。
     *
     * <p>If the current thread's interrupted status is set when it enters
     * this method, or it is {@linkplain Thread#interrupt interrupted}
     * while waiting, it will continue to wait until signalled. When it finally
     * returns from this method its interrupted status will still
     * be set.
     *
     * 如果在进入此方法时设置了当前线程的中断状态，或者在等待时，线程被中断，那么在接到信号之前，它将继续等待。
     * 当最终从此方法返回时，仍然将设置其中断状态。
     *
     * <p><b>Implementation Considerations</b>
     *
     * <b>实现注意事项</b>
     *
     * <p>The current thread is assumed to hold the lock associated with this
     * {@code Condition} when this method is called.
     * It is up to the implementation to determine if this is
     * the case and if not, how to respond. Typically, an exception will be
     * thrown (such as {@link IllegalMonitorStateException}) and the
     * implementation must document that fact.
     *
     * 假定调用此方法时，当前线程保持了与此 Condition 有关联的锁。
     * 这取决于确定是否为这种情况以及不是时，如何对此作出响应的实现。
     * 通常，将抛出一个异常（比如 IllegalMonitorStateException）并且该实现必须对此进行记录。
     */
    void awaitUninterruptibly();

    /**
     * Causes the current thread to wait until it is signalled or interrupted,
     * or the specified waiting time elapses.
     *
     * 造成当前线程在接到信号、被中断或到达指定等待时间之前一直处于等待状态。
     *
     * <p>The lock associated with this condition is atomically
     * released and the current thread becomes disabled for thread scheduling
     * purposes and lies dormant until <em>one</em> of five things happens:
     * <ul>
     * <li>Some other thread invokes the {@link #signal} method for this
     * {@code Condition} and the current thread happens to be chosen as the
     * thread to be awakened; or
     * <li>Some other thread invokes the {@link #signalAll} method for this
     * {@code Condition}; or
     * <li>Some other thread {@linkplain Thread#interrupt interrupts} the
     * current thread, and interruption of thread suspension is supported; or
     * <li>The specified waiting time elapses; or
     * <li>A &quot;<em>spurious wakeup</em>&quot; occurs.
     * </ul>
     *
     * <p>In all cases, before this method can return the current thread must
     * re-acquire the lock associated with this condition. When the
     * thread returns it is <em>guaranteed</em> to hold this lock.
     *
     * 与此条件相关的锁以原子方式释放，并且出于线程调度的目的，将禁用当前线程，且在发生以下五种情况之一 以前，当前线程将一直处于休眠状态：
     *
     * 其他某个线程调用此 Condition 的 signal() 方法，并且碰巧将当前线程选为被唤醒的线程；或者
     * 其他某个线程调用此 Condition 的 signalAll() 方法；或者
     * 其他某个线程中断当前线程，且支持中断线程的挂起；或者
     * 已超过指定的等待时间；或者
     * 发生“虚假唤醒”。
     * 在所有情况下，在此方法可以返回当前线程之前，都必须重新获取与此条件有关的锁。在线程返回时，可以保证 它保持此锁。
     *
     * <p>If the current thread:
     * <ul>
     * <li>has its interrupted status set on entry to this method; or
     * <li>is {@linkplain Thread#interrupt interrupted} while waiting
     * and interruption of thread suspension is supported,
     * </ul>
     * then {@link InterruptedException} is thrown and the current thread's
     * interrupted status is cleared. It is not specified, in the first
     * case, whether or not the test for interruption occurs before the lock
     * is released.
     *
     * 如果当前线程：
     *
     * 在进入此方法时已经设置了该线程的中断状态；或者
     * 在支持等待和中断线程挂起时，线程被中断，
     * 则抛出 InterruptedException，并且清除当前线程的已中断状态。在第一种情况下，没有指定是否在释放锁之前发生中断测试。
     *
     * <p>The method returns an estimate of the number of nanoseconds
     * remaining to wait given the supplied {@code nanosTimeout}
     * value upon return, or a value less than or equal to zero if it
     * timed out. This value can be used to determine whether and how
     * long to re-wait in cases where the wait returns but an awaited
     * condition still does not hold. Typical uses of this method take
     * the following form:
     *
     * 在返回时，该方法返回了所剩毫微秒数的一个估计值，以等待所提供的 nanosTimeout 值的时间，
     * 如果超时，则返回一个小于等于 0 的值。
     * 可以用此值来确定在等待返回但某一等待条件仍不具备的情况下，是否要再次等待，以及再次等待的时间。
     * 此方法的典型用法采用以下形式：
     *
     *  <pre> {@code
     * boolean aMethod(long timeout, TimeUnit unit) {
     *   long nanos = unit.toNanos(timeout);
     *   lock.lock();
     *   try {
     *     while (!conditionBeingWaitedFor()) {
     *       if (nanos <= 0L)
     *         return false;
     *       nanos = theCondition.awaitNanos(nanos);
     *     }
     *     // ...
     *   } finally {
     *     lock.unlock();
     *   }
     * }}</pre>
     *
     * <p>Design note: This method requires a nanosecond argument so
     * as to avoid truncation errors in reporting remaining times.
     * Such precision loss would make it difficult for programmers to
     * ensure that total waiting times are not systematically shorter
     * than specified when re-waits occur.
     *
     * 设计注意事项：此方法需要一个 nanosecond 参数，以避免在报告剩余时间时出现截断错误。
     * 在发生重新等待时，这种精度损失使得程序员难以确保总的等待时间不少于指定等待时间。
     *
     * <p><b>Implementation Considerations</b>
     * <b>实现注意事项</b>
     *
     * <p>The current thread is assumed to hold the lock associated with this
     * {@code Condition} when this method is called.
     * It is up to the implementation to determine if this is
     * the case and if not, how to respond. Typically, an exception will be
     * thrown (such as {@link IllegalMonitorStateException}) and the
     * implementation must document that fact.
     *
     * 假定调用此方法时，当前线程保持了与此 Condition 有关联的锁。
     * 这取决于确定是否为这种情况以及不是时，如何对此作出响应的实现。
     * 通常会抛出一个异常（比如 IllegalMonitorStateException）并且该实现必须对此进行记录。
     *
     * <p>An implementation can favor responding to an interrupt over normal
     * method return in response to a signal, or over indicating the elapse
     * of the specified waiting time. In either case the implementation
     * must ensure that the signal is redirected to another waiting thread, if
     * there is one.
     *
     * 与响应某个信号而返回的普通方法相比，或者与指示所使用的指定等待时间相比，实现可能更喜欢响应某个中断。
     * 在任意一种情况下，实现必须确保信号被重定向到另一个等待线程（如果有的话）。
     *
     * @param nanosTimeout the maximum time to wait, in nanoseconds 等待的最长时间，以毫微秒（纳秒）为单位
     * @return an estimate of the {@code nanosTimeout} value minus
     *         the time spent waiting upon return from this method.
     *         A positive value may be used as the argument to a
     *         subsequent call to this method to finish waiting out
     *         the desired time.  A value less than or equal to zero
     *         indicates that no time remains.
     *         值减去花费在等待此方法的返回结果的时间的估算。
     *         正值可以用作对此方法进行后续调用的参数，来完成等待所需时间结束。小于等于零的值表示没有剩余时间。
     * @throws InterruptedException if the current thread is interrupted
     *         (and interruption of thread suspension is supported)
     *         如果当前线程被中断（并且支持中断线程挂起）
     */
    long awaitNanos(long nanosTimeout) throws InterruptedException;

    /**
     * Causes the current thread to wait until it is signalled or interrupted,
     * or the specified waiting time elapses. This method is behaviorally
     * equivalent to:
     *  <pre> {@code awaitNanos(unit.toNanos(time)) > 0}</pre>
     *
     *  造成当前线程在接到信号、被中断或到达指定等待时间之前一直处于等待状态。此方法在行为上等效于：
     *    awaitNanos(unit.toNanos(time)) > 0
     *
     * @param time the maximum time to wait
     *             最长等待时间
     * @param unit the time unit of the {@code time} argument
     *             参数的时间单位
     * @return {@code false} if the waiting time detectably elapsed
     *         before return from the method, else {@code true}
     *         如果在从此方法返回前检测到等待时间超时，则返回 false，否则返回 true
     * @throws InterruptedException if the current thread is interrupted
     *         (and interruption of thread suspension is supported)
     *         InterruptedException - 如果当前线程被中断（并且支持中断线程挂起）
     */
    boolean await(long time, TimeUnit unit) throws InterruptedException;

    /**
     * Causes the current thread to wait until it is signalled or interrupted,
     * or the specified deadline elapses.
     *
     * 造成当前线程在接到信号、被中断或到达指定最后期限之前一直处于等待状态。
     *
     * <p>The lock associated with this condition is atomically
     * released and the current thread becomes disabled for thread scheduling
     * purposes and lies dormant until <em>one</em> of five things happens:
     * <ul>
     * <li>Some other thread invokes the {@link #signal} method for this
     * {@code Condition} and the current thread happens to be chosen as the
     * thread to be awakened; or
     * <li>Some other thread invokes the {@link #signalAll} method for this
     * {@code Condition}; or
     * <li>Some other thread {@linkplain Thread#interrupt interrupts} the
     * current thread, and interruption of thread suspension is supported; or
     * <li>The specified deadline elapses; or
     * <li>A &quot;<em>spurious wakeup</em>&quot; occurs.
     * </ul>
     *
     * <p>In all cases, before this method can return the current thread must
     * re-acquire the lock associated with this condition. When the
     * thread returns it is <em>guaranteed</em> to hold this lock.
     *
     * 与此条件相关的锁以原子方式释放，并且出于线程调度的目的，将禁用当前线程，且在发生以下五种情况之一 以前，当前线程将一直处于休眠状态：
     *
     * 其他某个线程调用此 Condition 的 signal() 方法，并且碰巧将当前线程选为被唤醒的线程；或者
     * 其他某个线程调用此 Condition 的 signalAll() 方法；或者
     * 其他某个线程中断当前线程，且支持中断线程的挂起；或者
     * 指定的最后期限到了；或者
     * 发生“虚假唤醒”。
     * 在所有情况下，在此方法可以返回当前线程之前，都必须重新获取与此条件有关的锁。在线程返回时，可以保证 它保持此锁。
     *
     * <p>If the current thread:
     * <ul>
     * <li>has its interrupted status set on entry to this method; or
     * <li>is {@linkplain Thread#interrupt interrupted} while waiting
     * and interruption of thread suspension is supported,
     * </ul>
     * then {@link InterruptedException} is thrown and the current thread's
     * interrupted status is cleared. It is not specified, in the first
     * case, whether or not the test for interruption occurs before the lock
     * is released.
     *
     * 如果当前线程：
     *
     * 在进入此方法时已经设置了该线程的中断状态；或者
     * 在支持等待和中断线程挂起时，线程被中断，
     * 则抛出 InterruptedException，并且清除当前线程的已中断状态。
     * 在第一种情况下，没有指定是否在释放锁之前发生中断测试。
     *
     *
     * <p>The return value indicates whether the deadline has elapsed,
     * which can be used as follows:
     *
     * 返回值指示是否到达最后期限，使用方式如下：
     *
     *  <pre> {@code
     * boolean aMethod(Date deadline) {
     *   boolean stillWaiting = true;
     *   lock.lock();
     *   try {
     *     while (!conditionBeingWaitedFor()) {
     *       if (!stillWaiting)
     *         return false;
     *       stillWaiting = theCondition.awaitUntil(deadline);
     *     }
     *     // ...
     *   } finally {
     *     lock.unlock();
     *   }
     * }}</pre>
     *
     * <p><b>Implementation Considerations</b>
     *
     * 实现注意事项
     *
     * <p>The current thread is assumed to hold the lock associated with this
     * {@code Condition} when this method is called.
     * It is up to the implementation to determine if this is
     * the case and if not, how to respond. Typically, an exception will be
     * thrown (such as {@link IllegalMonitorStateException}) and the
     * implementation must document that fact.
     *
     * 假定调用此方法时，当前线程保持了与此 Condition 有关联的锁。
     * 这取决于确定是否为这种情况以及不是时，如何对此作出响应的实现。
     * 通常，将抛出一个异常（比如 IllegalMonitorStateException）并且该实现必须对此进行记录。
     *
     * <p>An implementation can favor responding to an interrupt over normal
     * method return in response to a signal, or over indicating the passing
     * of the specified deadline. In either case the implementation
     * must ensure that the signal is redirected to another waiting thread, if
     * there is one.
     *
     * 与响应某个信号而返回的普通方法相比，或者与指示是否到达指定最终期限相比，实现可能更喜欢响应某个中断。
     * 在任意一种情况下，实现必须确保信号被重定向到另一个等待线程（如果有的话）。
     *
     * @param deadline the absolute time to wait until
     *                 一直处于等待状态的绝对时间
     * @return {@code false} if the deadline has elapsed upon return, else
     *         {@code true}
     *         如果在返回时已经到达最后期限，则返回 false，否则返回 true
     * @throws InterruptedException if the current thread is interrupted
     *         (and interruption of thread suspension is supported)
     *          如果当前线程被中断（并且支持中断线程挂起）
     */
    boolean awaitUntil(Date deadline) throws InterruptedException;

    /**
     * Wakes up one waiting thread.
     *
     * 唤醒一个等待线程
     *
     * <p>If any threads are waiting on this condition then one
     * is selected for waking up. That thread must then re-acquire the
     * lock before returning from {@code await}.
     *
     * 如果所有的线程都在等待此条件，则选择其中的一个唤醒。在从 await 返回之前，该线程必须重新获取锁。
     *
     * <p><b>Implementation Considerations</b>
     *
     * 实现注意事项
     *
     * <p>An implementation may (and typically does) require that the
     * current thread hold the lock associated with this {@code
     * Condition} when this method is called. Implementations must
     * document this precondition and any actions taken if the lock is
     * not held. Typically, an exception such as {@link
     * IllegalMonitorStateException} will be thrown.
     *
     * 假定调用此方法时，当前线程保持了与此 Condition 有关联的锁。
     * 这取决于确定是否为这种情况以及不是时，如何对此作出响应的实现。
     * 通常，将抛出一个异常（比如 IllegalMonitorStateException）并且该实现必须对此进行记录。
     */
    void signal();

    /**
     * Wakes up all waiting threads.
     *
     * 唤醒所有等待线程。
     *
     * <p>If any threads are waiting on this condition then they are
     * all woken up. Each thread must re-acquire the lock before it can
     * return from {@code await}.
     *
     * 如果所有的线程都在等待此条件，则唤醒所有线程。在从 await 返回之前，每个线程都必须重新获取锁。
     *
     * <p><b>Implementation Considerations</b>
     *
     * 实现注意事项
     *
     * <p>An implementation may (and typically does) require that the
     * current thread hold the lock associated with this {@code
     * Condition} when this method is called. Implementations must
     * document this precondition and any actions taken if the lock is
     * not held. Typically, an exception such as {@link
     * IllegalMonitorStateException} will be thrown.
     * 假定调用此方法时，当前线程保持了与此 Condition 有关联的锁。
     * 这取决于确定是否为这种情况以及不是时，如何对此作出响应的实现。
     * 通常，将抛出一个异常（比如 IllegalMonitorStateException）并且该实现必须对此进行记录。
     */
    void signalAll();
}
