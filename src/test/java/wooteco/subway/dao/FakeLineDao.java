package wooteco.subway.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.dao.EmptyResultDataAccessException;
import wooteco.subway.domain.Line;

public class FakeLineDao implements LineDao {

    private static final int EXECUTED_COLUMN_COUNT_ONE = 1;
    private static final int EXECUTED_COLUMN_COUNT_NONE = 0;

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
        return new ArrayList<>(lines.values());
    }

    @Override
    public int deleteById(Long id) {
        if (lines.containsKey(id)) {
            lines.remove(id);
            return EXECUTED_COLUMN_COUNT_ONE;
        }
        return EXECUTED_COLUMN_COUNT_NONE;
    }

    @Override
    public Line findById(Long id) {
        if (!lines.containsKey(id)) {
            throw new EmptyResultDataAccessException(1);
        }
        return lines.get(id);
    }

    @Override
    public boolean existsByNameOrColor(Line line) {
        return lines.values()
                .stream()
                .anyMatch(it -> it.getName().equals(line.getName()) || it.getColor().equals(line.getColor()));
    }

    @Override
    public int update(Line updatingLine) {
        Long id = updatingLine.getId();
        if (lines.containsKey(id)) {
            lines.put(id, updatingLine);
            return 1;
        }
        return 0;
    }
}
