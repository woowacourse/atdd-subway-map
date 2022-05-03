package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

    public static boolean existsByName(String name) {
        return lines.stream()
                .map(Line::getName)
                .filter(it -> it.equals(name))
                .count() != 0;
    }

    public static void deleteAll() {
        lines = new ArrayList<>();
    }

    public static List<Line> findAll() {
        return Collections.unmodifiableList(lines);
    }

    public static boolean notExistsById(Long id) {
        System.out.println(lines.get(0).getId() + "exist!!!!");
        System.out.println(id);
        return lines.stream()
                .map(Line::getId)
                .filter(it -> it.equals(id))
                .count() == 0;
    }

    public static Line findById(Long id) {
        return lines.stream()
                .filter(line -> line.getId().equals(id))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
