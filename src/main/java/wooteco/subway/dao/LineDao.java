package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.ReflectionUtils;

import wooteco.subway.domain.Line;

public class LineDao {
    private static Long seq = 0L;
    private static Map<Long, Line> lines = new LinkedHashMap<>();

    public static Line save(Line line) {
        Line persistLine = createNewObject(line);
        lines.put(persistLine.getId(), persistLine);
        return persistLine;
    }

    public static boolean existByName(String name) {
        return lines.values()
            .stream()
            .anyMatch(line -> line.getName().equals(name));
    }

    private static Line createNewObject(Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
    }

    public static void deleteAll() {
        lines.clear();
        seq = 0L;
    }

    public static List<Line> findAll() {
        return new ArrayList<>(lines.values());
    }

    public static Line find(long id) {
        return lines.get(id);
    }
}
