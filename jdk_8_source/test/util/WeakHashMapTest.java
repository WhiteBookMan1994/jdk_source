package util;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.WeakHashMap;

/**
 * @author dingchenchen
 * @since 2020-06-14
 */
public class WeakHashMapTest {
    public static void main(String[] args) {
        //weakHashMap存储 学生-分数 映射
        WeakHashMap<Student, Integer> weakHashMap = new WeakHashMap<>();
        //小明和小华对象分别有强引用关联：xiaoMing 和 xiaoHua；小亮直接new的对象，没有强引用关系
        Student xiaoMing = new Student("小明", 9);
        Student xiaoHua = new Student("小华",8);
        weakHashMap.put(xiaoMing, 100);
        weakHashMap.put(xiaoHua, 86);
        weakHashMap.put(new Student("小亮",9), 59);

        System.out.println("GC 前：");
        System.out.println(weakHashMap);

        System.gc();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("GC 后：");
        System.out.println(weakHashMap);
    }

    public static class Student {
        private String name;
        private Integer age;

        public Student(String name, Integer age){
            this.name = name;
            this.age = age;
        }

        public String getName(){
            return this.name;
        }

        public Integer getAge(){
            return this.age;
        }

        @Override
        public int hashCode() {
            return name.hashCode() ^ age.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof Student) {
                return name.equals(((Student) obj).getName()) && age.equals(((Student) obj).getAge());
            }
            return false;
        }

        @Override
        public String toString() {
            return "{name:"+name+",age:"+age+"}";
        }
    }

    /**
     * WeakHashMap<Integer, String> 和 WeakHashMap<String,Integer> 值得玩味
     * */
    public void test1() {
        WeakHashMap<Integer, String> weakHashMap = new WeakHashMap<>();
        // Integer one = 1, two = 2;
        weakHashMap.put(1, "one");
        weakHashMap.put(2, "two");
        weakHashMap.put(new Integer(3),"three");
        System.out.println("GC 前：");
        System.out.println(weakHashMap);

        System.gc();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("GC 后：");
        System.out.println(weakHashMap);
    }
}
