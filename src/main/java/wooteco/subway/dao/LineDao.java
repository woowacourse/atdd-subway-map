package wooteco.subway.dao;

import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Line;
import wooteco.subway.exception.NotFoundException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class LineDao {

    private static Long seq = 0L;
    private final List<Line> lines = new ArrayList<>();

    public Line save(Line line) {
        Line persistLine = createNewObject(line);
        lines.add(persistLine);
        return persistLine;
    }

    private Line createNewObject(Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
    }

    public List<Line> findAll() {
        return List.copyOf(lines);
    }

    public Line findById(Long id) {
        return lines.stream()
                .filter(l -> l.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("id에 맞는 지하철 노선이 없습니다."));
    }

    public void clear() {
        lines.clear();
    }

    public void update(Line line) {
        Line findLine = findById(line.getId());
        findLine.setName(line.getName());
        findLine.setColor(line.getColor());
    }

    public void delete(Long id) {
        lines.remove(findById(id));
    }
}
