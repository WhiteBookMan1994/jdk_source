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
import java.util.function.Supplier;
import java.util.function.Consumer;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.BiFunction;
import java.util.concurrent.Executor;

/**
 * A stage of a possibly asynchronous computation, that performs an
 * action or computes a value when another CompletionStage completes.
 * A stage completes upon termination of its computation, but this may
 * in turn trigger other dependent stages.  The functionality defined
 * in this interface takes only a few basic forms, which expand out to
 * a larger set of methods to capture a range of usage styles: <ul>
 *
 * 可能异步计算的阶段，当另一个CompletionStage完成时，该阶段执行操作或计算值。
 * 一个阶段在其计算终止时完成，但是这又可能触发其他相关阶段。
 * 此接口中定义的功能仅采用几种基本形式，这些形式扩展为更大的方法集，以捕获各种使用样式：
 *
 * <li>The computation performed by a stage may be expressed as a
 * Function, Consumer, or Runnable (using methods with names including
 * <em>apply</em>, <em>accept</em>, or <em>run</em>, respectively)
 * depending on whether it requires arguments and/or produces results.
 * For example, {@code stage.thenApply(x -> square(x)).thenAccept(x ->
 * System.out.print(x)).thenRun(() -> System.out.println())}. An
 * additional form (<em>compose</em>) applies functions of stages
 * themselves, rather than their results. </li>
 *
 * 阶段执行的计算可以表示为Function，Consumer或Runnable（使用名称包括
 *   * <em> apply </ em>，<em> accept </ em>或<em> run </ em>），具体取决于它是否需要参数和/或产生结果。
 *   例如，stage.thenApply（x-> square（x））.thenAccept（x-> System.out.print（x））.thenRun（（）-> System.out.println（））}。
 *   另一种形式（<em> compose </ em>）应用阶段本身的功能，而不是其结果。
 *
 * <li> One stage's execution may be triggered by completion of a
 * single stage, or both of two stages, or either of two stages.
 * Dependencies on a single stage are arranged using methods with
 * prefix <em>then</em>. Those triggered by completion of
 * <em>both</em> of two stages may <em>combine</em> their results or
 * effects, using correspondingly named methods. Those triggered by
 * <em>either</em> of two stages make no guarantees about which of the
 * results or effects are used for the dependent stage's
 * computation.</li>
 *
 *  一个阶段的执行可以通过完成一个阶段，或两个阶段的完成，或两个阶段中的任一阶段来触发。
 *  使用带有前缀<em> then </ em>的方法来安排对单个阶段的依赖关系
 *  两个阶段可以combine自己的计算，使用相应的both或者combine相关命名方法。
 *  either操作由两个阶段中的任一阶段触发的那些不能保证结果或效果中的哪一个用于依赖阶段的计算。
 *
 * <li> Dependencies among stages control the triggering of
 * computations, but do not otherwise guarantee any particular
 * ordering. Additionally, execution of a new stage's computations may
 * be arranged in any of three ways: default execution, default
 * asynchronous execution (using methods with suffix <em>async</em>
 * that employ the stage's default asynchronous execution facility),
 * or custom (via a supplied {@link Executor}).  The execution
 * properties of default and async modes are specified by
 * CompletionStage implementations, not this interface. Methods with
 * explicit Executor arguments may have arbitrary execution
 * properties, and might not even support concurrent execution, but
 * are arranged for processing in a way that accommodates asynchrony.
 *
 * 阶段之间的依赖关系控制计算的触发，但不能保证任何特定的顺序。
 * 此外，新阶段的计算的执行可以以三种方式中的任何一种来安排：
 * 默认执行、默认异步执行（使用后缀<em>async</em>的方法，使用stage的默认异步执行工具）或自定义（通过提供的{@link Executor}）。
 * default和async模式的执行属性由CompletionStage实现指定，而不是此接口。
 * 具有显式Executor参数的方法可能具有任意的执行属性，甚至可能不支持并发执行，但它们的处理方式可以适应异步性。
 *
 * <li> Two method forms support processing whether the triggering
 * stage completed normally or exceptionally: Method {@link
 * #whenComplete whenComplete} allows injection of an action
 * regardless of outcome, otherwise preserving the outcome in its
 * completion. Method {@link #handle handle} additionally allows the
 * stage to compute a replacement result that may enable further
 * processing by other dependent stages.  In all other cases, if a
 * stage's computation terminates abruptly with an (unchecked)
 * exception or error, then all dependent stages requiring its
 * completion complete exceptionally as well, with a {@link
 * CompletionException} holding the exception as its cause.  If a
 * stage is dependent on <em>both</em> of two stages, and both
 * complete exceptionally, then the CompletionException may correspond
 * to either one of these exceptions.  If a stage is dependent on
 * <em>either</em> of two others, and only one of them completes
 * exceptionally, no guarantees are made about whether the dependent
 * stage completes normally or exceptionally. In the case of method
 * {@code whenComplete}, when the supplied action itself encounters an
 * exception, then the stage exceptionally completes with this
 * exception if not already completed exceptionally.</li>
 *
 * 两种方法形式支持处理触发阶段是否正常或异常完成：
 * 方法whenComplete允许注入动作而不考虑结果，否则保留完成结果。
 * 方法handle还允许阶段计算可以使其他依赖阶段进一步处理的替换结果。
 * 在所有其他情况下，如果一个阶段的计算以（未检查）的异常或错误突然终止，那么所有依赖阶段都要求完成异常， CompletionException将异常作为其原因。
 * 如果一个阶段是依赖于两个both阶段，都异常完成，那么CompletionException可以对应于这些异常中的任何一个。
 * 如果一个阶段依赖于两个其他的任何一个either，并且只有一个异常完成，则不能保证依赖阶段是否正常或异常完成。
 * 在方法whenComplete的情况下，如果提供的操作本身遇到异常，则该阶段将以该异常异常完成（如果尚未异常完成）。
 *
 * </ul>
 *
 * <p>All methods adhere to the above triggering, execution, and
 * exceptional completion specifications (which are not repeated in
 * individual method specifications). Additionally, while arguments
 * used to pass a completion result (that is, for parameters of type
 * {@code T}) for methods accepting them may be null, passing a null
 * value for any other parameter will result in a {@link
 * NullPointerException} being thrown.
 *
 * 所有方法都遵循上述触发、执行和异常完成规范（在单个方法规范中不重复）。
 * 此外，虽然用于为接受它们的方法传递完成结果（即{@code T}类型的参数）的参数可能为null
 * ，但为任何其他参数传递null值将导致抛出{@link NullpointerException}。
 *
 * <p>This interface does not define methods for initially creating,
 * forcibly completing normally or exceptionally, probing completion
 * status or results, or awaiting completion of a stage.
 * Implementations of CompletionStage may provide means of achieving
 * such effects, as appropriate.  Method {@link #toCompletableFuture}
 * enables interoperability among different implementations of this
 * interface by providing a common conversion type.
 * 该接口没有定义初始创建方法，强制完成正常或异常的探测完成状态或结果，或等待阶段完成。
 * CompletionStage的实现类可能会提供适当的方式来实现这种效果。
 * 方法toCompletableFuture()通过提供公共转换类型实现了该接口的不同实现之间的互操作性。
 *
 * @author Doug Lea
 * @since 1.8
 */
public interface CompletionStage<T> {

    /**
     * Returns a new CompletionStage that, when this stage completes
     * normally, is executed with this stage's result as the argument
     * to the supplied function.
     *
     * 返回一个新的CompletionStage，当此阶段正常完成时，将以该阶段的结果作为所提供函数的参数执行。
     *
     * See the {@link CompletionStage} documentation for rules
     * covering exceptional completion.
     *
     * 请参阅CompletionStage文档，了解覆盖异常完成的规则。
     *
     * @param fn the function to use to compute the value of
     * the returned CompletionStage 用于计算返回的CompletionStage的值的函数
     * @param <U> the function's return type 函数的返回类型
     * @return the new CompletionStage 新的CompletionStage
     */
    public <U> CompletionStage<U> thenApply(Function<? super T,? extends U> fn);

    /**
     * Returns a new CompletionStage that, when this stage completes
     * normally, is executed using this stage's default asynchronous
     * execution facility, with this stage's result as the argument to
     * the supplied function.
     * 返回一个新的CompletionStage，当该阶段正常完成时，将使用此阶段的默认异步执行工具执行此阶段的结果作为所提供函数的参数。
     *
     * See the {@link CompletionStage} documentation for rules
     * covering exceptional completion.
     *
     * 请参阅CompletionStage有关异常完成规则的文档。
     *
     * @param fn the function to use to compute the value of
     * the returned CompletionStage
     * @param <U> the function's return type
     * @return the new CompletionStage
     */
    public <U> CompletionStage<U> thenApplyAsync
        (Function<? super T,? extends U> fn);

    /**
     * Returns a new CompletionStage that, when this stage completes
     * normally, is executed using the supplied Executor, with this
     * stage's result as the argument to the supplied function.
     *
     * 返回一个新的CompletionStage，当此阶段正常完成时，将使用提供的Executor执行此阶段的结果作为提供函数的参数。
     * 请参阅CompletionStage文档， 了解异常完成的规则。
     *
     * See the {@link CompletionStage} documentation for rules
     * covering exceptional completion.
     *
     * @param fn the function to use to compute the value of
     * the returned CompletionStage 用于计算返回的CompletionStage的值的函数
     * @param executor the executor to use for asynchronous execution 执行器用于异步执行
     * @param <U> the function's return type
     * @return the new CompletionStage
     */
    public <U> CompletionStage<U> thenApplyAsync
        (Function<? super T,? extends U> fn,
         Executor executor);

    /**
     * Returns a new CompletionStage that, when this stage completes
     * normally, is executed with this stage's result as the argument
     * to the supplied action.
     *
     * 返回一个新的CompletionStage，当此阶段正常完成时，将以该阶段的结果作为Consumer的参数执行。
     *
     * See the {@link CompletionStage} documentation for rules
     * covering exceptional completion.
     *
     * @param action the action to perform before completing the
     * returned CompletionStage 完成返回的CompletionStage之前要执行的操作
     * @return the new CompletionStage
     */
    public CompletionStage<Void> thenAccept(Consumer<? super T> action);

    /**
     * Returns a new CompletionStage that, when this stage completes
     * normally, is executed using this stage's default asynchronous
     * execution facility, with this stage's result as the argument to
     * the supplied action.
     *
     * 返回一个新的CompletionStage，当此阶段正常完成时，将使用此阶段的默认异步执行工具执行，此阶段的结果作为Consumer的参数。
     *
     * See the {@link CompletionStage} documentation for rules
     * covering exceptional completion.
     *
     * @param action the action to perform before completing the
     * returned CompletionStage
     * @return the new CompletionStage
     */
    public CompletionStage<Void> thenAcceptAsync(Consumer<? super T> action);

    /**
     * Returns a new CompletionStage that, when this stage completes
     * normally, is executed using the supplied Executor, with this
     * stage's result as the argument to the supplied action.
     *
     * 返回一个新的CompletionStage，当此阶段正常完成时，将使用提供的Executor执行此阶段的结果作为Consumer的参数。
     *
     * See the {@link CompletionStage} documentation for rules
     * covering exceptional completion.
     *
     * @param action the action to perform before completing the
     * returned CompletionStage
     * @param executor the executor to use for asynchronous execution
     * @return the new CompletionStage
     */
    public CompletionStage<Void> thenAcceptAsync(Consumer<? super T> action,
                                                 Executor executor);
    /**
     * Returns a new CompletionStage that, when this stage completes
     * normally, executes the given action.
     *
     * 返回一个新的CompletionStage，当此阶段正常完成时，执行给定的操作。
     *
     * See the {@link CompletionStage} documentation for rules
     * covering exceptional completion.
     *
     * @param action the action to perform before completing the
     * returned CompletionStage
     * @return the new CompletionStage
     */
    public CompletionStage<Void> thenRun(Runnable action);

    /**
     * Returns a new CompletionStage that, when this stage completes
     * normally, executes the given action using this stage's default
     * asynchronous execution facility.
     *
     * 返回一个新的CompletionStage，当此阶段正常完成时，使用此阶段的默认异步执行工具执行给定的操作。
     *
     * See the {@link CompletionStage} documentation for rules
     * covering exceptional completion.
     *
     * @param action the action to perform before completing the
     * returned CompletionStage
     * @return the new CompletionStage
     */
    public CompletionStage<Void> thenRunAsync(Runnable action);

    /**
     * Returns a new CompletionStage that, when this stage completes
     * normally, executes the given action using the supplied Executor.
     *
     * 返回一个新的CompletionStage，当此阶段正常完成时，使用提供的Executor执行给定的操作。
     *
     * See the {@link CompletionStage} documentation for rules
     * covering exceptional completion.
     *
     * @param action the action to perform before completing the
     * returned CompletionStage
     * @param executor the executor to use for asynchronous execution
     * @return the new CompletionStage
     */
    public CompletionStage<Void> thenRunAsync(Runnable action,
                                              Executor executor);

    /**
     * Returns a new CompletionStage that, when this and the other
     * given stage both complete normally, is executed with the two
     * results as arguments to the supplied function.
     *
     * 返回一个新的CompletionStage，当这个和另一个给定的阶段都正常完成时，两个结果作为提供函数的参数执行。
     *
     * See the {@link CompletionStage} documentation for rules
     * covering exceptional completion.
     *
     * @param other the other CompletionStage
     * @param fn the function to use to compute the value of
     * the returned CompletionStage
     * @param <U> the type of the other CompletionStage's result
     * @param <V> the function's return type
     * @return the new CompletionStage
     */
    public <U,V> CompletionStage<V> thenCombine
        (CompletionStage<? extends U> other,
         BiFunction<? super T,? super U,? extends V> fn);

    /**
     * Returns a new CompletionStage that, when this and the other
     * given stage complete normally, is executed using this stage's
     * default asynchronous execution facility, with the two results
     * as arguments to the supplied function.
     *
     * 返回一个新的CompletionStage，当这个和另一个给定阶段正常完成时，将使用此阶段的默认异步执行工具执行，其中两个结果作为提供函数的参数
     *
     * See the {@link CompletionStage} documentation for rules
     * covering exceptional completion.
     *
     * @param other the other CompletionStage
     * @param fn the function to use to compute the value of
     * the returned CompletionStage
     * @param <U> the type of the other CompletionStage's result
     * @param <V> the function's return type
     * @return the new CompletionStage
     */
    public <U,V> CompletionStage<V> thenCombineAsync
        (CompletionStage<? extends U> other,
         BiFunction<? super T,? super U,? extends V> fn);

    /**
     * Returns a new CompletionStage that, when this and the other
     * given stage complete normally, is executed using the supplied
     * executor, with the two results as arguments to the supplied
     * function.
     *
     * 返回一个新的CompletionStage，当这个和另一个给定阶段正常完成时，使用提供的执行器执行，其中两个结果作为提供的函数的参数。
     *
     * See the {@link CompletionStage} documentation for rules
     * covering exceptional completion.
     *
     * @param other the other CompletionStage
     * @param fn the function to use to compute the value of
     * the returned CompletionStage
     * @param executor the executor to use for asynchronous execution
     * @param <U> the type of the other CompletionStage's result
     * @param <V> the function's return type
     * @return the new CompletionStage
     */
    public <U,V> CompletionStage<V> thenCombineAsync
        (CompletionStage<? extends U> other,
         BiFunction<? super T,? super U,? extends V> fn,
         Executor executor);

    /**
     * Returns a new CompletionStage that, when this and the other
     * given stage both complete normally, is executed with the two
     * results as arguments to the supplied action.
     *
     * 返回一个新的CompletionStage，当这个和另一个给定的阶段都正常完成时，两个结果作为提供的操作的参数被执行。
     *
     * See the {@link CompletionStage} documentation for rules
     * covering exceptional completion.
     *
     * @param other the other CompletionStage
     * @param action the action to perform before completing the
     * returned CompletionStage
     * @param <U> the type of the other CompletionStage's result
     * @return the new CompletionStage
     */
    public <U> CompletionStage<Void> thenAcceptBoth
        (CompletionStage<? extends U> other,
         BiConsumer<? super T, ? super U> action);

    /**
     * Returns a new CompletionStage that, when this and the other
     * given stage complete normally, is executed using this stage's
     * default asynchronous execution facility, with the two results
     * as arguments to the supplied action.
     *
     * 返回一个新的CompletionStage，当这个和另一个给定阶段正常完成时，将使用此阶段的默认异步执行工具执行，其中两个结果作为提供的操作的参数。
     *
     * @param other the other CompletionStage
     * @param action the action to perform before completing the
     * returned CompletionStage
     * @param <U> the type of the other CompletionStage's result
     * @return the new CompletionStage
     */
    public <U> CompletionStage<Void> thenAcceptBothAsync
        (CompletionStage<? extends U> other,
         BiConsumer<? super T, ? super U> action);

    /**
     * Returns a new CompletionStage that, when this and the other
     * given stage complete normally, is executed using the supplied
     * executor, with the two results as arguments to the supplied
     * function.
     *
     * 返回一个新的CompletionStage，当这个和另一个给定阶段正常完成时，使用提供的执行器执行，其中两个结果作为提供的函数的参数。
     *
     * @param other the other CompletionStage
     * @param action the action to perform before completing the
     * returned CompletionStage
     * @param executor the executor to use for asynchronous execution
     * @param <U> the type of the other CompletionStage's result
     * @return the new CompletionStage
     */
    public <U> CompletionStage<Void> thenAcceptBothAsync
        (CompletionStage<? extends U> other,
         BiConsumer<? super T, ? super U> action,
         Executor executor);

    /**
     * Returns a new CompletionStage that, when this and the other
     * given stage both complete normally, executes the given action.
     *
     * 返回一个新的CompletionStage，当这个和另一个给定的阶段都正常完成时，执行给定的动作
     *
     * See the {@link CompletionStage} documentation for rules
     * covering exceptional completion.
     *
     * @param other the other CompletionStage
     * @param action the action to perform before completing the
     * returned CompletionStage
     * @return the new CompletionStage
     */
    public CompletionStage<Void> runAfterBoth(CompletionStage<?> other,
                                              Runnable action);
    /**
     * Returns a new CompletionStage that, when this and the other
     * given stage complete normally, executes the given action using
     * this stage's default asynchronous execution facility.
     *
     * 返回一个新的CompletionStage，当这个和另一个给定阶段正常完成时，使用此阶段的默认异步执行工具执行给定的操作。
     *
     * See the {@link CompletionStage} documentation for rules
     * covering exceptional completion.
     *
     * @param other the other CompletionStage
     * @param action the action to perform before completing the
     * returned CompletionStage
     * @return the new CompletionStage
     */
    public CompletionStage<Void> runAfterBothAsync(CompletionStage<?> other,
                                                   Runnable action);

    /**
     * Returns a new CompletionStage that, when this and the other
     * given stage complete normally, executes the given action using
     * the supplied executor.
     *
     * 返回一个新的CompletionStage，当这个和另一个给定阶段正常完成时，使用提供的执行器执行给定的操作。
     *
     * See the {@link CompletionStage} documentation for rules
     * covering exceptional completion.
     *
     * @param other the other CompletionStage
     * @param action the action to perform before completing the
     * returned CompletionStage
     * @param executor the executor to use for asynchronous execution
     * @return the new CompletionStage
     */
    public CompletionStage<Void> runAfterBothAsync(CompletionStage<?> other,
                                                   Runnable action,
                                                   Executor executor);
    /**
     * Returns a new CompletionStage that, when either this or the
     * other given stage complete normally, is executed with the
     * corresponding result as argument to the supplied function.
     *
     * 返回一个新的CompletionStage，当这个或另一个给定阶段正常完成时，执行相应的结果作为提供的函数的参数。
     *
     * See the {@link CompletionStage} documentation for rules
     * covering exceptional completion.
     *
     * @param other the other CompletionStage
     * @param fn the function to use to compute the value of
     * the returned CompletionStage
     * @param <U> the function's return type
     * @return the new CompletionStage
     */
    public <U> CompletionStage<U> applyToEither
        (CompletionStage<? extends T> other,
         Function<? super T, U> fn);

    /**
     * Returns a new CompletionStage that, when either this or the
     * other given stage complete normally, is executed using this
     * stage's default asynchronous execution facility, with the
     * corresponding result as argument to the supplied function.
     *
     * 返回一个新的CompletionStage，当这个或另一个给定阶段正常完成时，
     * 将使用此阶段的默认异步执行工具执行，其中相应的结果作为提供函数的参数。
     *
     * See the {@link CompletionStage} documentation for rules
     * covering exceptional completion.
     *
     * @param other the other CompletionStage
     * @param fn the function to use to compute the value of
     * the returned CompletionStage
     * @param <U> the function's return type
     * @return the new CompletionStage
     */
    public <U> CompletionStage<U> applyToEitherAsync
        (CompletionStage<? extends T> other,
         Function<? super T, U> fn);

    /**
     * Returns a new CompletionStage that, when either this or the
     * other given stage complete normally, is executed using the
     * supplied executor, with the corresponding result as argument to
     * the supplied function.
     * 返回一个新的CompletionStage，当这个或另一个给定阶段正常完成时，将使用提供的执行器执行，
     * 其中相应的结果作为参数提供给函数
     *
     * See the {@link CompletionStage} documentation for rules
     * covering exceptional completion.
     *
     * @param other the other CompletionStage
     * @param fn the function to use to compute the value of
     * the returned CompletionStage
     * @param executor the executor to use for asynchronous execution
     * @param <U> the function's return type
     * @return the new CompletionStage
     */
    public <U> CompletionStage<U> applyToEitherAsync
        (CompletionStage<? extends T> other,
         Function<? super T, U> fn,
         Executor executor);

    /**
     * Returns a new CompletionStage that, when either this or the
     * other given stage complete normally, is executed with the
     * corresponding result as argument to the supplied action.
     *
     * 返回一个新的CompletionStage，当这个或另一个给定阶段正常完成时，执行相应的结果作为提供的操作的参数。
     *
     * See the {@link CompletionStage} documentation for rules
     * covering exceptional completion.
     *
     * @param other the other CompletionStage
     * @param action the action to perform before completing the
     * returned CompletionStage
     * @return the new CompletionStage
     */
    public CompletionStage<Void> acceptEither
        (CompletionStage<? extends T> other,
         Consumer<? super T> action);

    /**
     * Returns a new CompletionStage that, when either this or the
     * other given stage complete normally, is executed using this
     * stage's default asynchronous execution facility, with the
     * corresponding result as argument to the supplied action.
     *
     * 返回一个新的CompletionStage，当这个或另一个给定阶段正常完成时，
     * 将使用此阶段的默认异步执行工具执行，其中相应的结果作为提供的操作的参数。
     *
     * See the {@link CompletionStage} documentation for rules
     * covering exceptional completion.
     *
     * @param other the other CompletionStage
     * @param action the action to perform before completing the
     * returned CompletionStage
     * @return the new CompletionStage
     */
    public CompletionStage<Void> acceptEitherAsync
        (CompletionStage<? extends T> other,
         Consumer<? super T> action);

    /**
     * Returns a new CompletionStage that, when either this or the
     * other given stage complete normally, is executed using the
     * supplied executor, with the corresponding result as argument to
     * the supplied function.
     *
     * 返回一个新的CompletionStage，当这个或另一个给定阶段正常完成时，
     * 将使用提供的执行器执行，其中相应的结果作为参数提供给函数。
     *
     * See the {@link CompletionStage} documentation for rules
     * covering exceptional completion.
     *
     * @param other the other CompletionStage
     * @param action the action to perform before completing the
     * returned CompletionStage
     * @param executor the executor to use for asynchronous execution
     * @return the new CompletionStage
     */
    public CompletionStage<Void> acceptEitherAsync
        (CompletionStage<? extends T> other,
         Consumer<? super T> action,
         Executor executor);

    /**
     * Returns a new CompletionStage that, when either this or the
     * other given stage complete normally, executes the given action.
     *
     * 返回一个新的CompletionStage，当这个或另一个给定阶段正常完成时，执行给定的操作
     *
     * See the {@link CompletionStage} documentation for rules
     * covering exceptional completion.
     *
     * @param other the other CompletionStage
     * @param action the action to perform before completing the
     * returned CompletionStage
     * @return the new CompletionStage
     */
    public CompletionStage<Void> runAfterEither(CompletionStage<?> other,
                                                Runnable action);

    /**
     * Returns a new CompletionStage that, when either this or the
     * other given stage complete normally, executes the given action
     * using this stage's default asynchronous execution facility.
     *
     * 返回一个新的CompletionStage，当这个或另一个给定阶段正常完成时，使用此阶段的默认异步执行工具执行给定的操作。
     *
     * See the {@link CompletionStage} documentation for rules
     * covering exceptional completion.
     *
     * @param other the other CompletionStage
     * @param action the action to perform before completing the
     * returned CompletionStage
     * @return the new CompletionStage
     */
    public CompletionStage<Void> runAfterEitherAsync
        (CompletionStage<?> other,
         Runnable action);

    /**
     * Returns a new CompletionStage that, when either this or the
     * other given stage complete normally, executes the given action
     * using the supplied executor.
     *
     * 返回一个新的CompletionStage，当这个或另一个给定阶段正常完成时，使用提供的执行器执行给定的操作。
     *
     * See the {@link CompletionStage} documentation for rules
     * covering exceptional completion.
     *
     * @param other the other CompletionStage
     * @param action the action to perform before completing the
     * returned CompletionStage
     * @param executor the executor to use for asynchronous execution
     * @return the new CompletionStage
     */
    public CompletionStage<Void> runAfterEitherAsync
        (CompletionStage<?> other,
         Runnable action,
         Executor executor);

    /**
     * Returns a new CompletionStage that, when this stage completes
     * normally, is executed with this stage as the argument
     * to the supplied function.
     *
     * 返回一个新的CompletionStage，当这个阶段正常完成时，这个阶段将作为提供函数的参数执行。
     *
     * See the {@link CompletionStage} documentation for rules
     * covering exceptional completion.
     *
     * @param fn the function returning a new CompletionStage
     * @param <U> the type of the returned CompletionStage's result
     * @return the CompletionStage
     */
    public <U> CompletionStage<U> thenCompose
        (Function<? super T, ? extends CompletionStage<U>> fn);

    /**
     * Returns a new CompletionStage that, when this stage completes
     * normally, is executed using this stage's default asynchronous
     * execution facility, with this stage as the argument to the
     * supplied function.
     *
     * 返回一个新的CompletionStage，当此阶段正常完成时，将使用此阶段的默认异步执行工具执行，此阶段作为提供的函数的参数。
     *
     * See the {@link CompletionStage} documentation for rules
     * covering exceptional completion.
     *
     * @param fn the function returning a new CompletionStage
     * @param <U> the type of the returned CompletionStage's result
     * @return the CompletionStage
     */
    public <U> CompletionStage<U> thenComposeAsync
        (Function<? super T, ? extends CompletionStage<U>> fn);

    /**
     * Returns a new CompletionStage that, when this stage completes
     * normally, is executed using the supplied Executor, with this
     * stage's result as the argument to the supplied function.
     *
     * 返回一个新的CompletionStage，当此阶段正常完成时，将使用提供的执行程序执行此阶段的结果作为提供函数的参数
     *
     * See the {@link CompletionStage} documentation for rules
     * covering exceptional completion.
     *
     * @param fn the function returning a new CompletionStage
     * @param executor the executor to use for asynchronous execution
     * @param <U> the type of the returned CompletionStage's result
     * @return the CompletionStage
     */
    public <U> CompletionStage<U> thenComposeAsync
        (Function<? super T, ? extends CompletionStage<U>> fn,
         Executor executor);

    /**
     * Returns a new CompletionStage that, when this stage completes
     * exceptionally, is executed with this stage's exception as the
     * argument to the supplied function.  Otherwise, if this stage
     * completes normally, then the returned stage also completes
     * normally with the same value.
     *
     * 返回一个新的CompletionStage，当此阶段完成异常时，将以此阶段的异常作为提供函数的参数执行。
     * 否则，如果此阶段正常完成，则返回的阶段也将以相同的值正常完成。
     *
     * @param fn the function to use to compute the value of the
     * returned CompletionStage if this CompletionStage completed
     * exceptionally
     * @return the new CompletionStage
     */
    public CompletionStage<T> exceptionally
        (Function<Throwable, ? extends T> fn);

    /**
     * Returns a new CompletionStage with the same result or exception as
     * this stage, that executes the given action when this stage completes.
     *
     * <p>When this stage is complete, the given action is invoked with the
     * result (or {@code null} if none) and the exception (or {@code null}
     * if none) of this stage as arguments.  The returned stage is completed
     * when the action returns.  If the supplied action itself encounters an
     * exception, then the returned stage exceptionally completes with this
     * exception unless this stage also completed exceptionally.
     *
     * 返回与此阶段相同的结果或异常的新的CompletionStage，当此阶段完成时，将使用结果（或 null如果没有））
     * 和此阶段的异常（或 null如果没有））执行给定操作。
     *
     * 当此阶段完成时，将使用该阶段的结果（如果没有则返回{@code null}）
     * 和该阶段的异常（如果没有则返回{@code null}）作为参数来调用给定操作。 动作返回时，返回的阶段已完成。
     * 如果所提供的操作本身遇到异常，那么除非此阶段也异常完成，否则返回阶段将异常终止。
     *
     * @param action the action to perform
     * @return the new CompletionStage
     */
    public CompletionStage<T> whenComplete
        (BiConsumer<? super T, ? super Throwable> action);

    /**
     * Returns a new CompletionStage with the same result or exception as
     * this stage, that executes the given action using this stage's
     * default asynchronous execution facility when this stage completes.
     *
     * 返回一个与此阶段相同结果或异常的新CompletionStage，当此阶段完成时，执行给定操作将使用此阶段的默认异步执行工具执行给定操作，
     * 结果（或 null如果没有））和异常（或 null如果没有）作为参数。
     *
     * <p>When this stage is complete, the given action is invoked with the
     * result (or {@code null} if none) and the exception (or {@code null}
     * if none) of this stage as arguments.  The returned stage is completed
     * when the action returns.  If the supplied action itself encounters an
     * exception, then the returned stage exceptionally completes with this
     * exception unless this stage also completed exceptionally.
     *
     * @param action the action to perform
     * @return the new CompletionStage
     */
    public CompletionStage<T> whenCompleteAsync
        (BiConsumer<? super T, ? super Throwable> action);

    /**
     * Returns a new CompletionStage with the same result or exception as
     * this stage, that executes the given action using the supplied
     * Executor when this stage completes.
     *
     * 返回与此阶段相同结果或异常的新CompletionStage，当此阶段完成时，
     * 使用提供的执行程序执行给定的操作，如果没有，则使用结果（或 null如果没有））和异常（或 null如果没有））作为论据。
     *
     * <p>When this stage is complete, the given action is invoked with the
     * result (or {@code null} if none) and the exception (or {@code null}
     * if none) of this stage as arguments.  The returned stage is completed
     * when the action returns.  If the supplied action itself encounters an
     * exception, then the returned stage exceptionally completes with this
     * exception unless this stage also completed exceptionally.
     *
     * @param action the action to perform
     * @param executor the executor to use for asynchronous execution
     * @return the new CompletionStage
     */
    public CompletionStage<T> whenCompleteAsync
        (BiConsumer<? super T, ? super Throwable> action,
         Executor executor);

    /**
     * Returns a new CompletionStage that, when this stage completes
     * either normally or exceptionally, is executed with this stage's
     * result and exception as arguments to the supplied function.
     *
     * 返回一个新的CompletionStage，当此阶段正常或异常完成时，将使用此阶段的结果和异常作为所提供函数的参数执行。
     * 当完成作为参数时，使用结果（或null如果没有））和此阶段的异常（或null如果没有））调用给定函数。
     *
     * <p>When this stage is complete, the given function is invoked
     * with the result (or {@code null} if none) and the exception (or
     * {@code null} if none) of this stage as arguments, and the
     * function's result is used to complete the returned stage.
     *
     * 当这个阶段完成时，用这个阶段的结果（如果没有，{@code null}）和异常（如果没有，{@code null}）作为参数来调用给定的函数，
     * 并使用函数的结果来完成返回的阶段。
     *
     * @param fn the function to use to compute the value of the
     * returned CompletionStage
     * @param <U> the function's return type
     * @return the new CompletionStage
     */
    public <U> CompletionStage<U> handle
        (BiFunction<? super T, Throwable, ? extends U> fn);

    /**
     * Returns a new CompletionStage that, when this stage completes
     * either normally or exceptionally, is executed using this stage's
     * default asynchronous execution facility, with this stage's
     * result and exception as arguments to the supplied function.
     *
     * 返回一个新的CompletionStage，当该阶段完成正常或异常时，将使用此阶段的默认异步执行工具执行，此阶段的结果和异常作为提供函数的参数。
     * 当完成作为参数时，使用结果（或null如果没有））和此阶段的异常（或null如果没有））调用给定函数。
     *
     * <p>When this stage is complete, the given function is invoked
     * with the result (or {@code null} if none) and the exception (or
     * {@code null} if none) of this stage as arguments, and the
     * function's result is used to complete the returned stage.
     *
     * @param fn the function to use to compute the value of the
     * returned CompletionStage
     * @param <U> the function's return type
     * @return the new CompletionStage
     */
    public <U> CompletionStage<U> handleAsync
        (BiFunction<? super T, Throwable, ? extends U> fn);

    /**
     * Returns a new CompletionStage that, when this stage completes
     * either normally or exceptionally, is executed using the
     * supplied executor, with this stage's result and exception as
     * arguments to the supplied function.
     *
     * 返回一个新的CompletionStage，当此阶段完成正常或异常时，将使用提供的执行程序执行此阶段的结果和异常作为提供的函数的参数。
     * 当完成作为参数时，使用结果（或null如果没有））和此阶段的异常（或null如果没有））调用给定函数。
     *
     * <p>When this stage is complete, the given function is invoked
     * with the result (or {@code null} if none) and the exception (or
     * {@code null} if none) of this stage as arguments, and the
     * function's result is used to complete the returned stage.
     *
     * @param fn the function to use to compute the value of the
     * returned CompletionStage
     * @param executor the executor to use for asynchronous execution
     * @param <U> the function's return type
     * @return the new CompletionStage
     */
    public <U> CompletionStage<U> handleAsync
        (BiFunction<? super T, Throwable, ? extends U> fn,
         Executor executor);

    /**
     * Returns a {@link CompletableFuture} maintaining the same
     * completion properties as this stage. If this stage is already a
     * CompletableFuture, this method may return this stage itself.
     * Otherwise, invocation of this method may be equivalent in
     * effect to {@code thenApply(x -> x)}, but returning an instance
     * of type {@code CompletableFuture}. A CompletionStage
     * implementation that does not choose to interoperate with others
     * may throw {@code UnsupportedOperationException}.
     *
     * 返回一个CompletableFuture，保持与这个阶段相同的完成属性。 如果这个阶段已经是一个CompletableFuture，那么这个方法本身就可以返回这个阶段。
     * 否则，调用此方法可能相当于thenApply(x -> x) ，但返回一个类型为CompletableFuture的实例。
     * 没有选择与他人互操作的CompletionStage实现可能会UnsupportedOperationException
     *
     * @return the CompletableFuture
     * @throws UnsupportedOperationException if this implementation
     * does not interoperate with CompletableFuture
     */
    public CompletableFuture<T> toCompletableFuture();

}
