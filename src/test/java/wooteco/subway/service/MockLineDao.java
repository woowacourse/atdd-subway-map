package wooteco.subway.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;

public class MockLineDao implements LineDao {

    private final Map<Long, Line> mockDb = new HashMap<>();
    private long sequenceId = 1;

    @Override
    public Long save(Line line) {
        Long id = sequenceId;
        mockDb.put(sequenceId++, new Line(id, line.getName(), line.getColor()));
        return id;
    }

    @Override
    public List<Line> findAll() {
        return new ArrayList<>(mockDb.values());
    }

    @Override
    public Line findById(Long id) {
        return mockDb.get(id);
    }

    @Override
    public Long update(Long id, Line line) {
        mockDb.put(id, createLine(id, line));
        return id;
    }

    @Override
    public void deleteById(Long id) {
        mockDb.remove(id);
    }

    private Line createLine(Long id, Line line) {
        return new Line(id, line.getName(), line.getColor());
    }
}
