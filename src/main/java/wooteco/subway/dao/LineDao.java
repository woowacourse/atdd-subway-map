package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Line;

public class LineDao {

    private static Long seq = 0L;
    private final static List<Line> lines = new ArrayList<>();

    public static Long save(Line line) {
        final Line newLine = createNewObject(line);
        lines.add(newLine);

        return newLine.getId();
    }

    public static Line findById(Long savedId) {
        return lines.stream()
                .filter(line -> line.getId().equals(savedId))
                .findAny()
                .orElseThrow(IllegalArgumentException::new);
    }

    private static Line createNewObject(Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
    }

    public static List<Line> findAll() {
        return List.copyOf(lines);
    }

    public static void deleteAll() {
        lines.clear();
    }

    public static Long updateById(Line updateLine) {
        final Line findLine = lines.stream()
                .filter(line -> line.getId().equals(updateLine.getId()))
                .findAny()
                .orElseThrow(IllegalArgumentException::new);
        lines.remove(findLine);
        lines.add(updateLine);

        return updateLine.getId();
    }
}
