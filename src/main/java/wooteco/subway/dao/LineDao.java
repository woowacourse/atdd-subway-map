package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Line;

public class LineDao {
    private static final String LINE_VALIDATION = "이미 등록된 노선입니다.";
    private static Long seq = 0L;
    private static List<Line> lines = new ArrayList<>();

    public static Line save(Line line) {
        validateDuplication(line);

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

    public static Line findById(Long id) {
        return lines.stream()
                .filter(line -> line.isSameId(id))
                .findAny()
                .orElseThrow();
    }

    public static void updateById(Long id, Line line) {
        validateDuplication(line);

        Line originLine = findById(id);
        originLine.updateLine(line.getName(), line.getColor());
    }

    public static void deleteById(Long id) {
        Line line = findById(id);
        lines.remove(line);
    }

    private static void validateDuplication(Line line) {
        if (lines.contains(line)) {
            throw new IllegalArgumentException(LINE_VALIDATION);
        }
    }
}
