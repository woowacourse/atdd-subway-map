package wooteco.subway.service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.ReflectionUtils;

import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;

public class FakeLineDao implements LineDao {
    private Long seq = 0L;
    private Map<Long, Line> lines = new LinkedHashMap<>();

    @Override
    public Line save(Line line) {
        Line persistLine = createNewObject(line);
        lines.put(persistLine.getId(), persistLine);
        return persistLine;
    }

    @Override
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

    @Override
    public List<Line> findAll() {
        return new ArrayList<>(lines.values());
    }

    @Override
    public Line find(Long id) {
        return lines.get(id);
    }

    @Override
    public boolean existById(Long id) {
        return lines.containsKey(id);
    }

    @Override
    public void update(Line line) {
        lines.put(line.getId(), line);
    }

    @Override
    public void delete(Long id) {
        lines.remove(id);
    }
}


