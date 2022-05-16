package wooteco.subway.dao.memory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Line;
import wooteco.subway.exception.NoSuchLineException;

public class LineMemoryDao {
    private static Long seq = 0L;
    private static final List<Line> lines = new ArrayList<>();

    public static Long save(final Line line) {
        checkDuplicateName(line);

        Line persistStation = createNewObject(line);
        lines.add(persistStation);
        return persistStation.getId();
    }

    public static Long update(final Long id, final String name, final String color) {
        Line line = findById(id);
        line.update(name, color);
        return id;
    }

    public static List<Line> findAll() {
        return lines;
    }

    public static Line findById(final long id) {
        return lines.stream()
                .filter(line -> line.getId() == id)
                .findFirst()
                .orElseThrow(() -> new NoSuchLineException(id));
    }

    public static void deleteAll() {
        lines.clear();
    }

    public static void deleteById(final Long id) {
        checkExistId(id);

        lines.removeIf(line -> line.getId().equals(id));
    }

    private static void checkExistId(final long id) {
        boolean hasSameId = lines.stream()
                .anyMatch(line -> line.getId() == id);
        if (!hasSameId) {
            throw new NoSuchLineException(id);
        }
    }

    private static void checkDuplicateName(final Line line) {
        boolean isDuplicateName = lines.stream()
                .anyMatch(it -> it.getName().equals(line.getName()));
        if (isDuplicateName) {
            throw new IllegalArgumentException("같은 이름을 가진 노선이 이미 있습니다.");
        }
    }

    private static Line createNewObject(final Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
    }
}
