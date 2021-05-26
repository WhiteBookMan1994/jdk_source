package util;

import org.junit.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * TreeMap 的经典使用场景：
 * 1、一致性Hash ：详见RocketMQ或者dubbo 负载均衡策略一致性Hash策略
 * 2、滑动窗口：RocketMQ 消费者端并发消费本地保存的offset（保存消费线程中最小的未消费的那个为滑动窗口的左侧）
 * @author dingchenchen
 * @since 2020-06-30
 */
public class TreeMapTest {

    public static void main(String[] args) {
        /** 默认构造方法，没有指定比较器Comparator，使用自然排序Comparable，会报NullPointerException*/
/*        Map<Student, Grade> map = new TreeMap<>();
        map.put(new Student("snow_white"), new Grade(99, "English"));
        map.put(null, new Grade());
        map.put(new Student("pear_snow"), new Grade(100, "English"));

        System.out.println(map);*/

        Map<Student, Grade> map1 = new TreeMap<>(new StudentComparator());
        map1.put(null, new Grade());
        map1.put(new Student("pear_snow"), new Grade(100, "English"));
        map1.put(new Student("snow_white"), new Grade(99, "English"));
        System.out.println(map1);
    }

    public static class Student implements Comparable<Student>{
        private String name;

        public Student(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public int compareTo(Student s) {
            return name.compareTo(s.name);
        }

        @Override
        public String toString(){
            return "{student=" + name + "}";
        }
    }

    public static class Grade {
        private Integer point;
        private String subject;

        public Grade(){}

        public Grade(Integer point, String subject) {
            this.point = point;
            this.subject = subject;
        }

        @Override
        public String toString(){
            return "{point=" + point + "," + "subject=" + subject +"}";
        }
    }

    /**
     * 自定义比较器，处理 key = null 的情况
     */
    public static class StudentComparator implements Comparator<Student> {

        @Override
        public int compare(Student o1, Student o2) {
            if (o1 == null || o2 == null) {
                return -1;
            }
            return o1.getName().compareTo(o2.getName());
        }
    }

    @Test
    public void testNullKey(){
        TreeMap treeMap = new TreeMap();
        treeMap.put(1, null);
        System.out.println(treeMap);
        System.out.println(Runtime.getRuntime().availableProcessors());
    }

    @Test
    public void testOrder(){
        //自然顺序：从小到大、从低到高
        TreeMap treeMap = new TreeMap();
        treeMap.put(1, "one");
        treeMap.put(3, "three");
        treeMap.put(2, "two");
        System.out.println(treeMap);
        //输出结果：{1=one, 2=two, 3=three}
        System.out.println(treeMap.firstKey());
        System.out.println(treeMap.lastKey());
    }

    @Test
    public void testConsistentHash(){
        /*
         * 模拟一致性Hash算法(是一个环)，假设有三台缓存机器以及对应IP
         * A: 192.168.0.1，存有缓存数据:缓存值key > z || A < 缓存值key < a的ASCII码
         * a: 192.168.0.2，存有缓存数据:a <= 缓存值key < z 的ASCII码
         * z: 192.168.0.3，存有缓存数据:缓存值key > z 的ASCII码
         */
        TreeMap<Integer, String> cacheMachines = new TreeMap<>();
        //这里直接用 A、B、C 对应的ASCII码作为机器在一致性Hash环上面的hash值
        //实际使用可以参考dubbo、rocketMQ的实现：md5->byte[]->Long值
        cacheMachines.put(Integer.valueOf('A'),"192.168.0.1");
        cacheMachines.put(Integer.valueOf('a'),"192.168.0.2");
        cacheMachines.put(Integer.valueOf('z'),"192.168.0.3");

        //缓存数据 B、a、b、200所在机器分别为 a、a、z、A
        System.out.println(consistentHash(cacheMachines, Integer.valueOf('B')));
        System.out.println(consistentHash(cacheMachines, Integer.valueOf('a')));
        System.out.println(consistentHash(cacheMachines, Integer.valueOf('b')));
        System.out.println(consistentHash(cacheMachines, 200));

        //删除A节点，缓存数据 B、a、b所对应机器都不会有影响
        System.out.println("---删除机器A---");
        cacheMachines.remove(Integer.valueOf('A'));
        System.out.println(consistentHash(cacheMachines, Integer.valueOf('B')));
        System.out.println(consistentHash(cacheMachines, Integer.valueOf('a')));
        System.out.println(consistentHash(cacheMachines, Integer.valueOf('b')));
        System.out.println(consistentHash(cacheMachines, 200));
    }

    public static String consistentHash(SortedMap<Integer,String> map, Integer key){
        SortedMap<Integer,String> subMap = map.tailMap(key);
        Integer nodeHash = subMap.isEmpty() ? map.firstKey() : subMap.firstKey();
        return map.get(nodeHash);
    }

}
