package wooteco.subway.dao;

import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Line;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class LineDao {
    private final static List<Line> lines = new ArrayList<>();
    private static Long seq = 0L;

    public static Line save(Line line) {
        Line persistLine = createNewObject(line);
        lines.add(persistLine);

        return persistLine;
    }

    public static List<Line> findAll() {
        return lines;
    }

    public static Line findById(Long id) {
        return lines.stream()
                .filter(it -> it.getId().equals(id))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 노선 ID입니다."));
    }

    private static Line createNewObject(Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);

        return line;
    }
}
