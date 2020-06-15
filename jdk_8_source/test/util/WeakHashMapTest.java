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
        /*//weakHashMap存储 学生-分数 映射
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
        System.out.println(weakHashMap);*/
        test1();
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
     * 字符串常量池
     * */
    public static void test1() {
        String four = "four", five = "five";
        WeakHashMap<String,Integer> weakHashMap1 = new WeakHashMap<>();
        weakHashMap1.put(four, 4);
        weakHashMap1.put(five, 5);
        weakHashMap1.put("six", 6);

        WeakHashMap<String,Integer> weakHashMap2 = new WeakHashMap<>();
        weakHashMap2.put(four, 4);
        weakHashMap2.put(five, 5);
        weakHashMap2.put(new String("six"), 6);

        System.out.println("GC 前：");
        System.out.println(weakHashMap1);
        System.out.println(weakHashMap2);

        System.gc();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("GC 后：");
        System.out.println(weakHashMap1);
        System.out.println(weakHashMap2);
    }
}
