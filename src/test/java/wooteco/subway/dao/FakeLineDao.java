package wooteco.subway.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import wooteco.subway.domain.Line;

public class FakeLineDao implements LineDao {

    private final List<Line> lines = new ArrayList<>();
    private Long seq = 0L;

    @Override
    public Line save(Line line) {
        Line persistStation = createNewObject(line);
        lines.add(persistStation);
        return persistStation;
    }

    private Line createNewObject(Line line) {
        return new Line(++seq, line.getName(), line.getColor());
    }

    @Override
    public Optional<Line> findById(Long id) {
        return lines.stream().filter(line -> line.getId().equals(id)).findFirst();
    }

    @Override
    public Optional<Line> findByName(String name) {
        return lines.stream()
            .filter(line -> name.equals(line.getName()))
            .findFirst();
    }

    @Override
    public List<Line> findAll() {
        return List.copyOf(lines);
    }

    @Override
    public void update(Line otherLine) {
        int idx = 0;
        for (Line line : lines) {
            if (line.hasSameId(otherLine)) {
                lines.set(idx, otherLine);
                return;
            }
            idx++;
        }
    }

    @Override
    public int deleteById(Long id) {
        if (!lines.removeIf(line -> line.getId().equals(id))) {
            return 0;
        }
        return 1;
    }
}
