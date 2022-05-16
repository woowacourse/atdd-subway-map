package wooteco.subway.util;

import java.lang.reflect.Field;
import java.util.Arrays;

import org.springframework.util.ReflectionUtils;

import wooteco.subway.exception.IdFieldNotFoundException;

public class SimpleReflectionUtils {

    public static <T> T injectId(T object, Long id) {
        final Class<?> clazz = object.getClass();
        final Field field = findIdAnnotatedField(clazz);
        field.setAccessible(true);
        ReflectionUtils.setField(field, object, id);
        return object;
    }

    private static Field findIdAnnotatedField(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
            .filter(field -> field.isAnnotationPresent(Id.class))
            .findAny()
            .orElseThrow(() -> new IdFieldNotFoundException("ID 필드가 존재하지 않습니다."));
    }
}
