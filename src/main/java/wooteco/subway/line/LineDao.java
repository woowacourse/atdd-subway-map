package wooteco.subway.line;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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

    public static Optional<Line> findById(Long id) {
        return lines.stream()
                .filter(line -> line.getId().equals(id))
                .findFirst();
    }

    public static void update(Long id, String color, String name) {
        if (!lines.removeIf(line -> line.getId().equals(id))) {
            throw new IllegalArgumentException("해당 이름의 노선이 존재하지 않습니다.");
        }
        lines.add(new Line(id, color, name));
    }

    public static void delete(Line line) {
        lines.remove(line);
    }

    private static Line createNewObject(Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
    }
}
