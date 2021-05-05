package wooteco.subway.line;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.station.Station;

public class LineDao {
    private static Long seq = 0L;
    private static List<Line> lines = new ArrayList<>();

    public static void clear() {
        lines.clear();
    }

    public static Line save(final Line line) {
        final Line persistLine = createNewObject(line);
        lines.add(persistLine);
        return persistLine;
    }

    private static Line createNewObject(final Line line) {
        final Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
    }

    public static Optional<Line> findById(final Long id) {
        return lines.stream()
            .filter(line -> line.isSameId(id))
            .findAny();
    }

    public static Optional<Line> findByName(final String name) {
        return lines.stream()
            .filter(line -> line.isSameName(name))
            .findAny();
    }

    public static List<Line> findALl() {
        return lines;
    }

    public static void deleteById(final Long id) {
        lines.removeIf(line -> line.isSameId(id));
    }
}
