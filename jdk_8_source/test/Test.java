import java.util.HashMap;

/**
 * @author dingchenchen
 * @since 2020-04-26
 */
public class Test {

    public static void main(String[] args) {
        String s1 = new String("hh");
        String s2 = new String("hh");
        String s3 = "hh";
        String s4 = "hh";
        System.out.println(s1==s2);
        System.out.println(s1==s3);
        System.out.println(s3==s4);
    }
}
