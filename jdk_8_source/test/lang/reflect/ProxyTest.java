package lang.reflect;





import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.UndeclaredThrowableException;

/**
 * JDK 动态代理使用案例
 * @author dingchenchen
 * @since 2021/2/4
 */
public class ProxyTest {
    public static void main(String[] args) {
        HelloServiceImpl helloImpl = new HelloServiceImpl();
        InvocationHandler handler = new MyInvocationHandler(helloImpl);
        HelloService helloService = (HelloService) Proxy.newProxyInstance(
                helloImpl.getClass().getClassLoader(), new Class[]{HelloService.class}, handler);
        helloService.sayHi("LiuYi");
    }
}

class MyInvocationHandler implements InvocationHandler {

    // 委托类对象，代理类实例把接口执行请求转发给它执行
    private Object obj;

    public MyInvocationHandler(Object o) {
        this.obj = o;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("begin...");
        /**
         * 生成的$Proxy类中的Method是来自interface中的定义，所以如果委托类obj没有实现接口interface，
         * 那么 method.invoke 方法执行是失败的
         * Exception in thread "main" java.lang.IllegalArgumentException: object is not an instance of declaring class
         * 	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
         * 	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
         * 	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
         * 	at java.lang.reflect.Method.invoke(Method.java:498)
         * 	at lang.reflect.MyInvocationHandler.invoke(ProxyTest.java:42)
         * 	at lang.reflect.$Proxy0.sayHi(Unknown Source)
         * 	at lang.reflect.ProxyTest.main(ProxyTest.java:22)
         */
        Object result = method.invoke(obj, args);
        System.out.println("result:" + result);
        System.out.println("end...");
        return result;
    }
}
/**
 * 委托类必须实现一组接口（至少一个interface），否则动态代理会有问题
 */
class HelloServiceImpl implements HelloService {

    @Override
    public String sayHi(String name) {
        System.out.println("Hi," + name);
        return "success";
    }
}

interface HelloService {

    String sayHi(String name);
}

/*
public interface Person {
    //上交班费
    void giveMoney();
}

以上述 Person 接口为例，反编译生成的$Proxy.class代理类，大致如下：

import java.lang.reflect.InvocationHandler;
        import java.lang.reflect.Method;
        import java.lang.reflect.Proxy;
        import java.lang.reflect.UndeclaredThrowableException;
        import proxy.Person;

public final class $Proxy0 extends Proxy implements Person
{
    private static Method m1;
    private static Method m2;
    private static Method m3;
    private static Method m0;

    */
/**
     *注意这里是生成代理类的构造方法，方法参数为InvocationHandler类型，看到这，是不是就有点明白
     *为何代理对象调用方法都是执行InvocationHandler中的invoke方法，而InvocationHandler又持有一个
     *被代理对象的实例，不禁会想难道是....？ 没错，就是你想的那样。
     *
     *super(paramInvocationHandler)，是调用父类Proxy的构造方法。
     *父类持有：protected InvocationHandler h;
     *Proxy构造方法：
     *    protected Proxy(InvocationHandler h) {
     *         Objects.requireNonNull(h);
     *         this.h = h;
     *     }
     *
     *//*

    public $Proxy0(InvocationHandler paramInvocationHandler)
            throws
    {
        super(paramInvocationHandler);
    }

    //这个静态块本来是在最后的，我把它拿到前面来，方便描述
    static
    {
        try
        {
            //看看这儿静态块儿里面有什么，是不是找到了giveMoney方法。请记住giveMoney通过反射得到的名字m3，其他的先不管
            m1 = Class.forName("java.lang.Object").getMethod("equals", new Class[] { Class.forName("java.lang.Object") });
            m2 = Class.forName("java.lang.Object").getMethod("toString", new Class[0]);
            m3 = Class.forName("proxy.Person").getMethod("giveMoney", new Class[0]);
            m0 = Class.forName("java.lang.Object").getMethod("hashCode", new Class[0]);
            return;
        }
        catch (NoSuchMethodException localNoSuchMethodException)
        {
            throw new NoSuchMethodError(localNoSuchMethodException.getMessage());
        }
        catch (ClassNotFoundException localClassNotFoundException)
        {
            throw new NoClassDefFoundError(localClassNotFoundException.getMessage());
        }
    }

    */
/**
     *
     *这里调用代理对象的giveMoney方法，直接就调用了InvocationHandler中的invoke方法，并把m3传了进去。
     *this.h.invoke(this, m3, null);这里简单，明了。
     *来，再想想，代理对象持有一个InvocationHandler对象，InvocationHandler对象持有一个被代理的对象，
     *再联系到InvacationHandler中的invoke方法。嗯，就是这样。
     *//*

    public final void giveMoney()
            throws
    {
        try
        {
            this.h.invoke(this, m3, null);
            return;
        }
        catch (Error|RuntimeException localError)
        {
            throw localError;
        }
        catch (Throwable localThrowable)
        {
            throw new UndeclaredThrowableException(localThrowable);
        }
    }

    //注意，这里为了节省篇幅，省去了toString，hashCode、equals方法的内容。原理和giveMoney方法一毛一样。

}*/
