import java.util.HashMap;

/**
 * @author dingchenchen
 * @since 2020-04-26
 */
public class Test {

    public static void main(String[] args) throws ClassNotFoundException {
        try {
            Class clazz = Class.forName("java.lang.String");
            System.out.println(clazz.getName()); // java.lang.String
            clazz = Class.forName("[Ljava.lang.String;");
            System.out.println(clazz.getName()); // [Ljava.lang.String;
            clazz = Class.forName("[D");
            System.out.println(clazz.getName()); // [D
            clazz = Class.forName("[I");
            System.out.println(clazz.getName()); // [I
        } catch(ClassNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println(Test[][][].class);
    }
}
