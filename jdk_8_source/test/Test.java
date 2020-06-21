import java.util.HashMap;

/**
 * @author dingchenchen
 * @since 2020-04-26
 */
public class Test {

    public static void main(String[] args) {
        System.out.println(System.identityHashCode(new Integer(1)));
        System.out.println(new Integer(1).hashCode());

        System.out.println(System.identityHashCode(new String("A")));
        System.out.println(new String("A").hashCode());
        System.out.println(new String("A").hashCode());
    }
}
