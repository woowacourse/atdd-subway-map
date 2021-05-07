package wooteco.subway.line;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.ReflectionUtils;

public class LineDao {
    private static Long seq = 0L;
    private static final List<Line> LINES = new ArrayList<>();

    public static Line save(Line line) {
        if (isDuplicateLineName(line)) {
            throw new IllegalArgumentException("이미 저장된 노선 이름입니다.");
        }

        Line persistLine = createNewObject(line);
        LINES.add(persistLine);
        return persistLine;
    }

    private static boolean isDuplicateLineName(Line line) {
        final String lineName = line.getName();
        return LINES.stream()
                    .anyMatch(storedLine -> storedLine.getName().equals(lineName));
    }

    public static List<Line> findAll() {
        return LINES;
    }

    public static Line findById(Long id) {
        return LINES.get(id.intValue());
    }

    private static Line createNewObject(Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
    }
}
