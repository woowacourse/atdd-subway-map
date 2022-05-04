package wooteco.subway.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import wooteco.subway.domain.Line;

@Repository
public class JdbcLineDao implements LineDao {

    private final List<Line> lines = new ArrayList<>();
    private long seq = 0L;

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
    public void update(Long id, String name, String color) {
        int idx = 0;
        for (Line line : lines) {
            if (line.getId().equals(id)) {
                lines.set(idx, new Line(id, name, color));
                return;
            }
            idx++;
        }
    }

    @Override
    public void deleteById(Long id) {
        if (!lines.removeIf(line -> line.getId().equals(id))) {
            throw new IllegalArgumentException("존재하지 않는 노선입니다.");
        }
    }

    @Override
    public void deleteAll() {
        lines.clear();
    }
}
