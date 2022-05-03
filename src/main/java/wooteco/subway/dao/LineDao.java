package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Line;

public class LineDao {

    private static final List<Line> lines = new ArrayList<>();

    private static Long seq = 0L;

    public static Line save(final Line line) {
        validateDuplicateName(line.getName());
        validateDuplicateColor(line.getColor());
        final Line persistLine = createNewObject(line);
        lines.add(persistLine);
        return persistLine;
    }

    private static void validateDuplicateName(final String name) {
        final boolean isDuplicate = lines.stream()
                .anyMatch(line -> line.isSameName(name));
        if (isDuplicate) {
            throw new IllegalArgumentException("같은 이름의 노선이 이미 존재합니다.");
        }
    }

    private static void validateDuplicateColor(final String color) {
        final boolean isDuplicate = lines.stream()
                .anyMatch(line -> line.isSameColor(color));
        if (isDuplicate) {
            throw new IllegalArgumentException("같은 색상의 노선이 이미 존재합니다.");
        }
    }

    private static Line createNewObject(final Line line) {
        final Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
    }

    public static List<Line> findAll() {
        return lines;
    }

    public static Line find(final Long id) {
        return lines.stream()
                .filter(line -> line.isSameId(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 노선입니다."));
    }

    public static Line update(final Long id, final String updateName, final String updateColor) {
        final Line line = find(id);
        line.update(updateName, updateColor);
        return line;
    }

    public static void delete(final Long id) {
        lines.remove(find(id));
    }
}
