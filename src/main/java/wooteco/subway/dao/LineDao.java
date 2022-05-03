package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Line;

public class LineDao {
    private Long seq = 0L;
    private final Map<Long, Line> lines;

    public LineDao() {
        lines = new HashMap<>();
    }

    public Line save(final Line line) {
        Line persistLine = createNewObject(line);
        lines.put(persistLine.getId(), persistLine);
        return persistLine;
    }

    private Line createNewObject(Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
    }
}
