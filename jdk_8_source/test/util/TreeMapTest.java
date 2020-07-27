package util;

import org.junit.Test;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

/**
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


}
