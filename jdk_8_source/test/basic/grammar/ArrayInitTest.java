package basic.grammar;

/**
 * @author dingchenchen
 * @since 2021/2/5
 */
public class ArrayInitTest {

    public static void main(String[] args) {
        // 初始化数组的方式
        // 完整方式：
         int [] a = new int[]{
                 1,2,3,4,5,6,7
         };
         // 简易方式：
        int [] b = {1,2,3,4,5,6};
        System.out.println(a.length);
        System.out.println(a.getClass());
        System.out.println(b.length);
    }
}
