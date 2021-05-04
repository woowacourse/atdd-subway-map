package wooteco.subway.line;

import org.springframework.util.ReflectionUtils;
import wooteco.subway.exception.LineDuplicationException;
import wooteco.subway.exception.NoLineException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class LineDao {

    private static Long seq = 0L;
    private static List<Line> lines = new ArrayList<>();

    public static Line save(Line line) {
        validateDuplicatedLine(line);
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

    private static void validateDuplicatedLine(Line newLine) {
        if (isDuplicatedName(newLine) || isDuplicatedColor(newLine)) {
            throw new LineDuplicationException();
        }
    }

    private static boolean isDuplicatedName(Line newLine) {
        return lines.stream()
                .anyMatch(line -> line.isSameName(newLine));
    }

    private static boolean isDuplicatedColor(Line newLine) {
        return lines.stream()
                .anyMatch(line -> line.isSameColor(newLine));
    }

    public static List<Line> findAll() {
        return lines;
    }

    public static void update(Long id, String name, String color) {
        Line line = findById(id);
        line = new Line(id, name, color);
    }

    private static Line findById(Long id) {
        return lines.stream()
            .filter(line -> line.isSameId(id))
            .findAny()
            .orElseThrow(NoLineException::new);
    }

    public static void delete(Long id) {
        lines.stream()
            .filter(line -> line.isSameId(id))
            .findAny()
            .ifPresent(line -> lines.remove(line));
    }
}
