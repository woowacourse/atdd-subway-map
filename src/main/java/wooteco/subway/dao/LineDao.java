package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
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

    public static List<Line> findAll() {
        return lines;
    }

    public static void deleteById(Long id) {
        lines.removeIf(it -> it.getId().equals(id));
    }

    public static Optional<Line> findById(Long id) {
        return lines.stream()
            .filter(it -> it.getId() == id)
            .findFirst();
    }


    private static Line createNewObject(Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
    }

    public static boolean exists(Line line) {
        return lines.stream()
            .anyMatch(it -> it.getName().equals(line.getName()) || it.getColor().equals(line.getColor()));
    }
}
