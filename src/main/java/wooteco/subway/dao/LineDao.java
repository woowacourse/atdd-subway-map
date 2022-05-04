package wooteco.subway.dao;

import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Line;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

public class LineDao {

    private static Long seq = 0L;
    private static List<Line> lines = new ArrayList<>();

    public static Line save(Line line) {
        Line persistLine = createNewObject(line);
        lines.add(line);
        return persistLine;
    }

    private static Line createNewObject(Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
    }

    public static Line findById(Long id) {
        return lines.stream()
                .filter(it -> it.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("[ERROR] 해당 노선을 찾을 수 없습니다."));
    }

    public static List<Line> findAll() {
        return Collections.unmodifiableList(lines);
    }

    public static boolean existLineByName(String name) {
        return lines.stream()
                .anyMatch(it -> it.getName().equals(name));
    }

    public static boolean existLineByColor(String color) {
        return lines.stream()
                .anyMatch(it -> it.getColor().equals(color));
    }

    public static void updateById(Long id, String name, String color) {
        Line line = findById(id);
        line.setName(name);
        line.setColor(color);
    }
}
