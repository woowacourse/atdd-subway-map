package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Line;

public class LineDao {
    private static Long seq = 0L;
    private static List<Line> lines = new ArrayList<>();

    public static Line save(Line line) {
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

    public static Line find(Long id) {
        return lines.stream()
                .filter(line -> line.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 노선입니다."));
    }

    public static List<Line> findAll() {
        return Collections.unmodifiableList(lines);
    }

    public static void update(Line line) {
        delete(line.getId());
        lines.add(line);
    }

    public static void delete(Long id) {
        boolean isRemoving = lines.removeIf(line -> line.getId().equals(id));
        if (!isRemoving) {
            throw new IllegalArgumentException("존재하지 않는 노선입니다.");
        }
    }
}
