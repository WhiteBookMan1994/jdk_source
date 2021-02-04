package lang.reflect;



import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

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
