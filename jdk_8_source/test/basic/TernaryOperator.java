package basic;

/**
 * @author dingchenchen
 * @since 2020/9/27
 */
public class TernaryOperator {
    public static void main(String[] args) {
        Integer a = 1;
        Integer b = 12;
        Integer c = null;
        Boolean flag = false;
        //a * b 的结果是int类型，那么c会强制拆成int类型，抛出NPE异常
        Integer result =  (flag ? a * b : c);
    }
}
