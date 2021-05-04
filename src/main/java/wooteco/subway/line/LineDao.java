package wooteco.subway.line;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LineDao {
    private static Long seq = 0L;
    private static List<Line> lines = new ArrayList<>();

    public static Line save(Line line) {
        Line persistentLine = createNewObject(line);
        lines.add(persistentLine);
        return persistentLine;
    }

    public static boolean findByName(String name) {
        return lines.stream()
                .anyMatch(line -> line.getName().equals(name));
    }

    public static List<Line> findAll() {
        return Collections.unmodifiableList(lines);
    }

    private static Line createNewObject(Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
    }
}
