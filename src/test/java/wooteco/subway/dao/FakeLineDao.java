package wooteco.subway.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.dao.EmptyResultDataAccessException;
import wooteco.subway.domain.Line;

public class FakeLineDao implements LineDao {

    private Long seq = 0L;
    private final Map<Long, Line> lines = new HashMap<>();

    @Override
    public Line save(Line line) {
        Long id = ++seq;
        Line l = new Line(id, line.getName(), line.getColor());
        lines.put(id, l);
        return lines.get(id);
    }

    @Override
    public List<Line> findAll() {
        return lines.values()
                .stream()
                .collect(Collectors.toList());
    }

    @Override
    public int deleteById(Long id) {
        if (lines.containsKey(id)) {
            lines.remove(id);
            return 1;
        }
        return 0;
    }

    @Override
    public Line findById(Long id) {
        if (!lines.containsKey(id)) {
            throw new EmptyResultDataAccessException(1);
        }
        return lines.get(id);
    }

    @Override
    public boolean exists(Line line) {
        return lines.values()
                .stream()
                .anyMatch(it -> it.getName().equals(line.getName()) || it.getColor().equals(line.getColor()));
    }

    @Override
    public int update(Long id, Line updatingLine) {
        if (lines.containsKey(id)) {
            lines.put(id, new Line(id, updatingLine.getName(), updatingLine.getColor()));
            return 1;
        }
        return 0;
    }
}
