package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Line;

public class LineDao {
    private static final String NO_ID_LINE_ERROR_MESSAGE = "해당 아이디의 노선이 없습니다.";
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

    private static Line createNewObject(Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
    }

    public static void update(Long id, Line line) {
        Line foundLine = lines.stream().filter(inLine -> Objects.equals(inLine.getId(), id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(NO_ID_LINE_ERROR_MESSAGE));
        foundLine.update(line);
    }

    public static void clear() {
        lines.clear();
        seq = 0L;
    }

    public static void delete(Long id) {
        Line result = lines.stream()
                .filter(line -> Objects.equals(line.getId(), id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(NO_ID_LINE_ERROR_MESSAGE));
        lines.remove(result);
    }
}
