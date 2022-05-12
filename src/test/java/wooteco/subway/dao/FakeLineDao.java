package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Line;

public class FakeLineDao implements LineDao {

    private Long seq = 0L;
    private Map<Long, Line> lines = new HashMap<>();

    @Override
    public Line insert(Line line) {
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

    @Override
    public Line findById(Long id) {
        return lines.get(id);
    }

    @Override
    public Boolean existByName(Line line) {
        return lines.values().stream()
                .anyMatch(it -> it.getName().equals(line.getName()));
    }

    @Override
    public Boolean existByColor(Line line) {
        return lines.values().stream()
                .anyMatch(it -> it.getColor().equals(line.getColor()));
    }

    @Override
    public List<Line> findAll() {
        return new ArrayList<>(lines.values());
    }

    @Override
    public void update(Long id, String name, String color) {
        lines.put(id, new Line(id, name, color));
    }

    @Override
    public void delete(Long id) {
        lines.remove(id);
    }
}
