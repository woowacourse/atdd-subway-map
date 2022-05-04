package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Line;

public class LineDao {

    private static Long seq = 0L;
    private static List<Line> lines = new ArrayList<>();

    public static Line save(Line line) {
        Line persistLine = createNewObject(line);
        lines.add(persistLine);
        return persistLine;
    }

    private static Line createNewObject(Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
    }

    public static Optional<Line> findByName(String name) {
        return lines.stream()
                .filter(line -> line.getName().equals(name))
                .findFirst();
    }

    public static void deleteAll() {
        lines = new ArrayList<>();
        seq = 0L;
    }

    public static List<Line> findAll() {
        return Collections.unmodifiableList(lines);
    }
}
