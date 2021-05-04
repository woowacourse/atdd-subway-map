package wooteco.subway.line.dao;

import org.springframework.util.ReflectionUtils;
import wooteco.subway.line.domain.Line;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LineDao {

    private static Long seq = 0L;
    private static List<Line> lines = new ArrayList<>();

    public static Line save(Line line) {
        Line persistLine = createNewObject(line);
        lines.add(persistLine);
        return persistLine;
    }

    public static Optional<Line> findById(Long id) {
        return lines.stream()
                .filter(line -> line.getId().equals(id))
                .findAny();
    }

    public static Optional<Line> findByName(String lineName) {
        return lines.stream()
                .filter(line -> line.getName().equals(lineName))
                .findAny();
    }

    public static List<Line> findAll() {
        return lines;
    }

    public static void delete(Long id) {
        lines.removeIf(line -> line.getId().equals(id));
    }

    public static void update(Line newLine) {
        lines.stream()
                .filter(line -> line.getId().equals(newLine.getId()))
                .map(line -> updateObject(line, newLine))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("수정할 대상이 없습니다."));
    }

    private static Line createNewObject(Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
    }

    private static Line updateObject(Line line, Line updateLine) {
        Field color = ReflectionUtils.findField(Line.class, "color");
        Field fare = ReflectionUtils.findField(Line.class, "extraFare");
        Field name = ReflectionUtils.findField(Line.class, "name");

        color.setAccessible(true);
        fare.setAccessible(true);
        name.setAccessible(true);

        ReflectionUtils.setField(color, line, updateLine.getColor());
        ReflectionUtils.setField(fare, line, updateLine.getExtraFare());
        ReflectionUtils.setField(name, line, updateLine.getName());
        return line;
    }
}
