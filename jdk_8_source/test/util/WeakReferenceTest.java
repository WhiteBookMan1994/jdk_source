package util;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

/**
 * @author dingchenchen
 * @since 2020-06-08
 */
public class WeakReferenceTest {

    public static void main(String[] args) {
        /* PearSnow pearSnow = new PearSnow(14);
         * 采用这种写法，相当于有一个强引用 pearSnow 引用new的对象PearSnow(14)，GC过程中pearSnow被视为GC Root
         * 必须有 pearSnow = null;才能达到new的对象PearSnow(14)只有弱引用了
         * 即注意Java中的四种引用类型针对的是new出来的对象，而非指向对象的指针变量
         */
        WeakReference<PearSnow> weakReference = new WeakReference<>(new PearSnow(14));
        System.out.println(weakReference.get().getAge());
        //pearSnow = null;
        System.gc();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (weakReference.get() == null) {
            System.out.println("已回收");
        }
    }


    public static class PearSnow {

        private Integer age;

        PearSnow(Integer age){
            this.age = age;
        }

        public Integer getAge() {
            return age;
        }

    }

    /**某面试题，输出结果：cde*/
    private static String test(){
        String a = new String("a");
        WeakReference<String> b = new WeakReference<String>(a);
        WeakHashMap<String, Integer> weakMap = new WeakHashMap<String, Integer>();
        weakMap.put(b.get(), 1);
        a = null;
        System.gc();
        String c = "";
        try{
            c = b.get().replace("a", "b");
            return c;
        }catch(Exception e){
            c = "c";
            return c;
        }finally{
            c += "d";
            return c + "e";
        }
    }
}
