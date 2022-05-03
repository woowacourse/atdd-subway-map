package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Line;

public class LineDao {
    private static Long seq = 0L;
    private static List<Line> lines = new ArrayList<>();

    public static Line save(Line line) {
        Line persistLine = createNewObject(line);
        validateDuplicateName(line.getName());
        lines.add(persistLine);
        return persistLine;
    }

    private static Line createNewObject(Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
    }

    private static void validateDuplicateName(String name) {
        boolean isDuplicate = lines.stream()
                .anyMatch(line -> line.isSameName(name));
        if (isDuplicate) {
            throw new IllegalArgumentException("이름이 중복된 노선은 만들 수 없습니다.");
        }
    }

    public static Line findById(Long id) {
        return lines.stream()
                .filter(line -> line.isSameId(id))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("해당 노선은 존재하지 않습니다."));
    }

    public static List<Line> findAll() {
        return lines;
    }

    public static void update(Long id, String name, String color) {
        delete(id);
        save(new Line(id, name, color));
    }

    public static void delete(Long id) {
        Line foundLine = lines.stream()
                .filter(line -> line.isSameId(id))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("해당 노선은 존재하지 않습니다."));
        lines.remove(foundLine);
    }

    public static void clear() {
        lines.clear();
    }
}
