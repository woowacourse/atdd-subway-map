package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Line;

public class LineDao {

    private static Long sequence = 0L;
    private static final List<Line> lines = new ArrayList<>();

    public static Line save(Line line) {
        validateDuplication(line);
        Line persistedLine = createUniqueId(line);
        lines.add(persistedLine);
        return persistedLine;
    }

    public static Line findById(Long id) {
        return lines.stream()
            .filter(value -> value.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("조회하려는 id가 없습니다."));
    }

    public static List<Line> findAll() {
        return List.copyOf(lines);
    }

    public static void modify(Long id, String name, String color) {
        if (isDuplicate(name, color)) {
            throw new IllegalArgumentException("노선의 이름과 색깔은 중복될 수 없습니다.");
        }
        findById(id).update(name, color);
    }

    public static void deleteById(Long id) {
        lines.removeIf(value -> value.getId().equals(id));
    }

    public static void deleteAll() {
        lines.clear();
    }

    private static void validateDuplication(Line line) {
        if (isDuplicate(line.getName(), line.getColor())) {
            throw new IllegalArgumentException("노선의 이름은 중복될 수 없습니다.");
        }
    }

    private static boolean isDuplicate(String name, String color) {
        return lines.stream()
            .anyMatch(value -> value.getName().equals(name) || value.getColor().equals(color));
    }

    private static Line createUniqueId(Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++sequence);
        return line;
    }
}
