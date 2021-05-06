package wooteco.subway.line;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class LineDao {
    private static Long seq = 0L;
    private static List<Line> lines = new ArrayList<>();

    public static Line save(Line line) {
        validateLine(line);
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

    private static void validateLine(Line newLine) {
        if (duplicatedNameExists(newLine.getName())) {
            throw new IllegalArgumentException("중복된 노선입니다.");
        }
    }

    private static boolean duplicatedNameExists(String newLine) {
        return lines.stream()
                .anyMatch(line -> line.getName().equals(newLine));
    }

    public static void clear() {
        lines.clear();
    }

    public static List<Line> findAll() {
        return lines;
    }
}
