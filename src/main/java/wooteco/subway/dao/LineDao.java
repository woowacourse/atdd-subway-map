package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.line.Line;

public class LineDao {

    private static Long seq = 0L;
    private static List<Line> lines = new ArrayList<>();

    public static Line save(Line Line) {
        Line persistLine = createNewObject(Line);
        lines.add(persistLine);
        return persistLine;
    }

    public static List<Line> findAll() {
        return lines;
    }

    public static Optional<Line> findById(long id) {
        return lines.stream()
            .filter(Line -> Line.getId() == id)
            .findAny();
    }

    private static Line createNewObject(Line Line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, Line, ++seq);
        return Line;
    }

    public static void deleteById(long id) {
        lines.removeIf(Line -> Line.getId() == id);
    }
}