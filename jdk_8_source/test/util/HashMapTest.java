package util;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dingchenchen
 * @since 2021/2/26
 */
public class HashMapTest {


    @Test
    public void testChineseKey() {
        //场景：使用 品牌+车系+车型 中文字符串作为map的key
        Map<String, Integer> cache = new HashMap<>();
        String key1 = "奥迪Q32017款 35 TFSI 时尚型";
        cache.put(key1, 1);
        // key2 与 key1 相同，key3 比 key2 多一个空格
        String key2 = "奥迪Q32017款 35 TFSI 时尚型";
        if (cache.containsKey(key2)) {
            cache.put(key2,cache.get(key2) + 1);
        } else {
            cache.put(key2, 1);
        }
        String key3 = "奥迪Q32017款 35  TFSI 时尚型";
        if (cache.containsKey(key3)) {
            cache.put(key3,cache.get(key3) + 1);
        } else {
            cache.put(key3, 1);
        }
        System.out.println(cache);
    }
}
