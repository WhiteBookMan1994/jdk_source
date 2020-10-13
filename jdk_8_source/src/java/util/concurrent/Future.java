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

package java.util.concurrent;

/**
 * A {@code Future} represents the result of an asynchronous
 * computation.  Methods are provided to check if the computation is
 * complete, to wait for its completion, and to retrieve the result of
 * the computation.  The result can only be retrieved using method
 * {@code get} when the computation has completed, blocking if
 * necessary until it is ready.  Cancellation is performed by the
 * {@code cancel} method.  Additional methods are provided to
 * determine if the task completed normally or was cancelled. Once a
 * computation has completed, the computation cannot be cancelled.
 * If you would like to use a {@code Future} for the sake
 * of cancellability but not provide a usable result, you can
 * declare types of the form {@code Future<?>} and
 * return {@code null} as a result of the underlying task.
 *
 * Future 接口代表异步计算的结果。它提供了用于检查计算是否完成、等待计算完成以及获取计算结果的方法。
 * 计算完成后只能使用 get 方法来获取结果，如有必要，计算完成前可以阻塞此方法。
 * 取消则由 cancel 方法来执行。Future接口还提供了其他方法，以确定任务是正常完成还是被取消了。
 * 一旦计算完成，就不能再取消计算。如果为了可取消性而使用 Future 但又不提供可用的结果，
 * 则可以声明 Future<?> 形式类型、并返回 null 作为底层任务的结果。
 *
 * <p>
 * <b>Sample Usage</b> (Note that the following classes are all
 * made-up.)
 * 用法示例（注意，下列各类都是构造好的。）:
 * <pre> {@code
 * interface ArchiveSearcher { String search(String target); }
 * class App {
 *   ExecutorService executor = ...
 *   ArchiveSearcher searcher = ...
 *   void showSearch(final String target)
 *       throws InterruptedException {
 *     Future<String> future
 *       = executor.submit(new Callable<String>() {
 *         public String call() {
 *             return searcher.search(target);
 *         }});
 *     displayOtherThings(); // do other things while searching 在搜索时做其他事情
 *     try {
 *       displayText(future.get()); // use future
 *     } catch (ExecutionException ex) { cleanup(); return; }
 *   }
 * }}</pre>
 *
 * The {@link FutureTask} class is an implementation of {@code Future} that
 * implements {@code Runnable}, and so may be executed by an {@code Executor}.
 * FutureTask 类是 Future接口的一个实现， 同时实现了 Runnable 接口，所以可通过 Executor 来执行。
 *
 * For example, the above construction with {@code submit} could be replaced by:
 * 例如，可用下列内容替换上面带有 submit 的构造：
 *  <pre> {@code
 * FutureTask<String> future =
 *   new FutureTask<String>(new Callable<String>() {
 *     public String call() {
 *       return searcher.search(target);
 *   }});
 * executor.execute(future);}</pre>
 *
 * <p>Memory consistency effects: Actions taken by the asynchronous computation
 * <a href="package-summary.html#MemoryVisibility"> <i>happen-before</i></a>
 * actions following the corresponding {@code Future.get()} in another thread.
 * 内存一致性效果：异步计算采取的操作  happen-before 另一线程中紧跟在相应的 Future.get() 之后的操作。
 *
 * @see FutureTask
 * @see Executor
 * @since 1.5
 * @author Doug Lea
 * @param <V> The result type returned by this Future's {@code get} method
 */
public interface Future<V> {

    /**
     * Attempts to cancel execution of this task.  This attempt will
     * fail if the task has already completed, has already been cancelled,
     * or could not be cancelled for some other reason. If successful,
     * and this task has not started when {@code cancel} is called,
     * this task should never run.  If the task has already started,
     * then the {@code mayInterruptIfRunning} parameter determines
     * whether the thread executing this task should be interrupted in
     * an attempt to stop the task.
     * 试图取消对此任务的执行。如果任务已完成、或已取消，或者由于某些其他原因而无法取消，则此尝试将失败。
     * 当调用 cancel 时，如果调用成功，而此任务尚未启动，则此任务将永不运行。
     * 如果任务已经启动，则 mayInterruptIfRunning 参数确定是否应该以试图停止任务的方式来中断执行此任务的线程。
     *
     * <p>After this method returns, subsequent calls to {@link #isDone} will
     * always return {@code true}.  Subsequent calls to {@link #isCancelled}
     * will always return {@code true} if this method returned {@code true}.
     * 此方法返回后，对 isDone() 的后续调用将始终返回 true。
     * 如果此方法返回 true，则对 isCancelled() 的后续调用将始终返回 true。
     *
     * @param mayInterruptIfRunning {@code true} if the thread executing this
     * task should be interrupted; otherwise, in-progress tasks are allowed
     * to complete 如果应该中断执行此任务的线程，则为 true；否则允许正在运行的任务运行完成
     * @return {@code false} if the task could not be cancelled,
     * typically because it has already completed normally;
     * {@code true} otherwise  如果无法取消任务，则返回 false，这通常是由于它已经正常完成；否则返回 true
     */
    boolean cancel(boolean mayInterruptIfRunning);

    /**
     * Returns {@code true} if this task was cancelled before it completed
     * normally.
     * 如果在任务正常完成前将其取消，则返回 true。
     * @return {@code true} if this task was cancelled before it completed
     */
    boolean isCancelled();

    /**
     * Returns {@code true} if this task completed.
     * 如果任务已完成，则返回 true。
     *
     * Completion may be due to normal termination, an exception, or
     * cancellation -- in all of these cases, this method will return
     * {@code true}.
     * 可能由于正常终止、异常或取消而完成，在所有这些情况中，此方法都将返回 true。
     *
     * @return {@code true} if this task completed 如果任务已完成，则返回 true
     */
    boolean isDone();

    /**
     * Waits if necessary for the computation to complete, and then
     * retrieves its result.
     * 如有必要，等待计算完成，然后获取其结果。【注意：这说明调用get()会阻塞当前线程直到获取到异步计算结果】
     *
     * @return the computed result 计算的结果
     * @throws CancellationException if the computation was cancelled 如果计算被取消
     * @throws ExecutionException if the computation threw an 如果计算抛出异常
     * exception
     * @throws InterruptedException if the current thread was interrupted 如果当前的线程在等待时被中断
     * while waiting
     */
    V get() throws InterruptedException, ExecutionException;

    /**
     * Waits if necessary for at most the given time for the computation
     * to complete, and then retrieves its result, if available.
     * 如有必要，最多等待为使计算完成所给定的时间之后，获取其结果（如果结果可用）
     *
     * @param timeout the maximum time to wait 等待的最大时间
     * @param unit the time unit of the timeout argument 参数的时间单位
     * @return the computed result 计算的结果
     * @throws CancellationException if the computation was cancelled 如果计算被取消
     * @throws ExecutionException if the computation threw an 如果计算抛出异常
     * exception
     * @throws InterruptedException if the current thread was interrupted 如果当前的线程在等待时被中断
     * while waiting
     * @throws TimeoutException if the wait timed out  如果等待超时
     */
    V get(long timeout, TimeUnit unit)
        throws InterruptedException, ExecutionException, TimeoutException;
}
