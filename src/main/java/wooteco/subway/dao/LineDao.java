package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.ReflectionUtils;

import wooteco.subway.domain.Line;

public class LineDao {

    private Long seq = 0L;
    private final List<Line> lines = new ArrayList<>();

    public Line save(Line line) {
        validateDistinct(line);
        Line persistLine = createNewObject(line);
        lines.add(persistLine);
        return persistLine;
    }

    private void validateDistinct(Line otherLine) {
        boolean isDuplicated = lines.stream()
            .anyMatch(station -> station.hasSameNameWith(otherLine));
        if (isDuplicated) {
            throw new IllegalStateException("이미 존재하는 노선 이름입니다.");
        }
    }

    private Line createNewObject(Line line) {
        final Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
    }
}
