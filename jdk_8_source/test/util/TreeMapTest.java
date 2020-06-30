package util;

import org.junit.Test;

import java.util.TreeMap;

/**
 * @author dingchenchen
 * @since 2020-06-30
 */
public class TreeMapTest {

    @Test
    public void testNullKey(){
        TreeMap treeMap = new TreeMap();
        treeMap.put(1, null);
        System.out.println(treeMap);
    }
}
