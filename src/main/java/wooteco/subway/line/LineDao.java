package wooteco.subway.line;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.util.ReflectionUtils;

public class LineDao {
    private static Long seq = 0L;
    private static List<Line> lines = new ArrayList<>();

    public static Line save(Line line) {
        Line persistsLine = createNewObject(line);
        if (isPersist(persistsLine)) {
            throw new IllegalArgumentException("이미 존재하는 노선입니다.");
        }
        lines.add(line);
        return persistsLine;
    }

    private static Line createNewObject(Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
    }

    private static boolean isPersist(Line persistsLine) {
        return lines.stream()
            .anyMatch(persistedLine -> persistedLine.getName().equals(persistsLine.getName()));
    }

    public static List<Line> findAll() {
        return Collections.unmodifiableList(lines);
    }

    public static Line findById(Long id) {
        return lines
            .stream()
            .filter(line -> line.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 노선입니다."));
    }

    public static void update(Line updatedLine) {
        deleteById(updatedLine.getId());
        lines.add(updatedLine);
    }

    public static void deleteById(Long id) {
        lines.removeIf(line -> line.getId().equals(id));
    }
}
