package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Line;

public class LineDao {

    private static Long sequence = 0L;
    private static final List<Line> lines = new ArrayList<>();

    public static Line save(Line line) {
        validateDuplication(line);
        Line persistedLine = createUniqueId(line);
        lines.add(persistedLine);
        return persistedLine;
    }

    public static void deleteAll() {
        lines.clear();
    }

    private static void validateDuplication(Line line) {
        if (lines.contains(line)) {
            throw new IllegalArgumentException("노선의 이름은 중복될 수 없습니다.");
        }
    }

    private static Line createUniqueId(Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++sequence);
        return line;
    }
}
