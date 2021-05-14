package wooteco.subway.common;

import java.lang.reflect.Field;

public class PersistenceUtils {

    public static <T> void insertId(T instance, Long id) {
        try {
            for (Field declaredField : instance.getClass().getDeclaredFields()) {
                declaredField.setAccessible(true);
                if (declaredField.isAnnotationPresent(Id.class)) {
                    declaredField.set(instance, id);
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
