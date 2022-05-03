package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Line;

public class LineDao {
    private static Long seq = 0L;
    private static List<Line> lines = new ArrayList<>();

    public static Line save(Line line) {
        Line persistLine = createNewObject(line);
        if (hasDuplicateLine(persistLine)) {
            throw new IllegalArgumentException("같은 이름의 노선이 존재합니다.");
        }
        lines.add(persistLine);
        return persistLine;
    }

    public static Line findById(Long id) {
        return lines.stream()
                .filter(line -> line.getId() == id)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("해당하는 노선이 존재하지 않습니다."));
    }

    public static List<Line> findAll() {
        return lines;
    }

    private static Line createNewObject(Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
    }

    private static boolean hasDuplicateLine(Line persistLine) {
        return lines.stream()
                .anyMatch(persistLine::isSameName);
    }

    public static Line updateById(Long id, String name, String color) {
        Line line = findById(id);
        line.setName(name);
        line.setColor(color);
        return line;
    }

    public static void deleteById(Long id) {
        boolean result = lines.removeIf(line -> line.getId() == id);
        if (!result) {
            throw new NoSuchElementException("해당하는 노선이 존재하지 않습니다.");
        }
    }
}
