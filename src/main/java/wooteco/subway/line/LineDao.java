package wooteco.subway.line;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.util.ReflectionUtils;

import wooteco.subway.exception.DuplicateNameException;

public class LineDao {
    private static Long seq = 0L;
    static final List<Line> LINES = new ArrayList<>();

    public static Line save(Line line) {
        if (isDuplicateLineName(line)) {
            throw new DuplicateNameException("이미 저장된 노선 이름입니다.");
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

    public static Optional<Line> findById(Long id) {
        return LINES.stream()
                    .filter(line -> line.getId().equals(id))
                    .findAny();
    }

    public static void update(Line beforeLine, Line afterLine) {
        final int updateIndex = LINES.indexOf(beforeLine);
        LINES.set(updateIndex, afterLine);
    }

    public static void delete(Long id) {
        LINES.removeIf(line -> line.getId().equals(id));
    }

    private static Line createNewObject(Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
    }
}
