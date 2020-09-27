import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.crypto.KeyGenerator;
/**
 * @author dingchenchen
 * @since 2020-04-26
 */
public class Test extends Test1{
    public static <S> Object getStateDescField(S s) {
        Class clazz = s.getClass();
        if (!clazz.isAnnotationPresent(StateConfig.class)) {
            return null;
        }
        StateConfig stateConfig = (StateConfig) clazz.getAnnotation(StateConfig.class);
        String descField = stateConfig.descField();
        try {
            Field field = clazz.getDeclaredField(descField);
            field.setAccessible(true);
            return field.get(s);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        System.out.println(Objects.equals(null, null));
    }
}
