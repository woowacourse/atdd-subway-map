package wooteco.subway.service;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.util.ReflectionUtils;

import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineEntity;

public class FakeLineDao implements LineDao {
    private Long seq = 0L;
    private Map<Long, Line> lines = new LinkedHashMap<>();

    @Override
    public LineEntity save(Line line) {
        Line persistLine = createNewObject(line);
        lines.put(persistLine.getId(), persistLine);
        return new LineEntity(persistLine.getId(), persistLine.getName(), persistLine.getColor());
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
    public List<LineEntity> findAll() {
        return lines.values()
            .stream()
            .map(line -> new LineEntity(line.getId(), line.getName(), line.getColor()))
            .collect(Collectors.toList());
    }

    @Override
    public LineEntity find(Long id) {
        Line line = lines.get(id);
        return new LineEntity(line.getId(), line.getName(), line.getColor());
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


