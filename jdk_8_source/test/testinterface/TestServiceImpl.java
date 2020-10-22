package testinterface;

/**
 * @author dingchenchen
 * @since 2020/10/22
 */
public class TestServiceImpl implements TestService{
    @Override
    public TestServiceImpl get() {//返回的是TestService的实现类TestServiceImpl,也是override
        return new TestServiceImpl();
    }
}
