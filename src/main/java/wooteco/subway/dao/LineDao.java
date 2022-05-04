package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.util.ReflectionUtils;

import wooteco.subway.domain.Line;

@Repository
public class LineDao {
    private Long seq = 0L;
    private Map<Long, Line> lines = new LinkedHashMap<>();

    public Line save(Line line) {
        Line persistLine = createNewObject(line);
        lines.put(persistLine.getId(), persistLine);
        return persistLine;
    }

    public boolean existByName(String name) {
        return lines.values()
            .stream()
            .anyMatch(line -> line.getName().equals(name));
    }

    private Line createNewObject(Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
    }

    public List<Line> findAll() {
        return new ArrayList<>(lines.values());
    }

    public Line find(Long id) {
        return lines.get(id);
    }

    public boolean existById(Long id) {
        return lines.containsKey(id);
    }

    public void update(Long id, Line line) {
        lines.put(id, line);
    }

    public void delete(Long id) {
        lines.remove(id);
    }
}
