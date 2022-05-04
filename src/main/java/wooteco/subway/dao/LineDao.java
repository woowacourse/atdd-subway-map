package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    public List<Line> findAll() {
        return List.copyOf(lines);
    }

    public Line findById(Long id) {
        return lines.stream()
            .filter(line -> Objects.equals(line.getId(), id))
            .findAny()
            .orElseThrow(() -> new IllegalStateException("조회하고자 하는 노선이 존재하지 않습니다."));
    }

    public void update(Line line) {
        final boolean isRemoved = lines.removeIf(it -> Objects.equals(it.getId(), line.getId()));
        if (!isRemoved) {
            throw new IllegalStateException("수정하고자 하는 노선이 존재하지 않습니다.");
        }
        lines.add(line);
    }

    public void delete(Long id) {
        final boolean isRemoved = lines.removeIf(it -> Objects.equals(it.getId(), id));
        if (!isRemoved) {
            throw new IllegalStateException("삭제하고자 하는 노선이 존재하지 않습니다.");
        }
    }
}
