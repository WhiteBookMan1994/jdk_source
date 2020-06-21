package util;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * @author dingchenchen
 * @since 2020-06-21
 */
public class IdentityHashMapTest {

    public static void main(String[] args) {
        Map<String, Integer> hashMap = new HashMap<>();
        hashMap.put(new String("one"), 1);
        hashMap.put(new String("one"), 2);
        hashMap.put(new String("one"), 3);
        System.out.println("hashMap:");
        System.out.println(hashMap);

        Map<String, Integer> identityHashMap = new IdentityHashMap<>();
        identityHashMap.put(new String("one"), 1);
        identityHashMap.put(new String("one"), 2);
        identityHashMap.put(new String("one"), 3);
        System.out.println("identityHashMap:");
        System.out.println(identityHashMap);

        System.out.println(System.identityHashCode(new Integer(1)));
        System.out.println(new Integer(1).hashCode());
        String s = "two";
        identityHashMap.put(s, 2);
        System.out.println(identityHashMap.get(s));
    }
}
