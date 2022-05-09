package wooteco.subway.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import wooteco.subway.domain.Line;

public class FakeLineDao implements LineDao {

    private Long seq = 0L;
    private final Map<Long, Line> lines = new HashMap<>();

    @Override
    public Long save(Line line) {
        validateDuplicateName(line);
        Line newLine = new Line(++seq, line.getName(), line.getColor());
        lines.put(seq, newLine);
        return seq;
    }

    private void validateDuplicateName(Line line) {
        if (lines.containsValue(line)) {
            throw new IllegalArgumentException("중복된 이름이 존재합니다.");
        }
    }

    @Override
    public List<Line> findAll() {
        return new ArrayList<>(lines.values());
    }

    @Override
    public boolean deleteById(Long lineId) {
        if (lines.containsKey(lineId)) {
            lines.remove(lineId);
            return true;
        }

        return false;
    }

    @Override
    public Optional<Line> findById(Long id) {
        return Optional.ofNullable(lines.get(id));
    }

    @Override
    public boolean updateById(Long savedId, Line line) {
        if (lines.containsKey(savedId)) {
            lines.replace(savedId, new Line(savedId, line.getName(), line.getColor()));
            return true;
        }

        return false;
    }

    @Override
    public boolean exists(Line line) {
        Optional<Line> presentLine = lines.values().stream()
            .filter(i -> i.getName().equals(line.getName()))
            .findAny();
        return presentLine.isPresent();
    }
}
