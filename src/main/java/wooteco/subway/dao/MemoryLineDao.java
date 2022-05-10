package wooteco.subway.dao;

import wooteco.subway.domain.Line;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class MemoryLineDao implements LineDao {

    private List<Line> lines = new ArrayList<>();

    private AtomicLong sequence = new AtomicLong();

    @Override
    public long save(Line line) {
        lines.add(new Line(sequence.incrementAndGet(), line.getName(), line.getColor()));
        return sequence.get();
    }

    @Override
    public Optional<Line> findById(Long id) {
        return lines.stream()
                .filter(line -> line.getId().equals(id))
                .findAny();
    }

    @Override
    public List<Line> findAll() {
        return Collections.unmodifiableList(lines);
    }

    @Override
    public boolean existById(Long id) {
        return lines.stream()
                .anyMatch(line -> line.getId().equals(id));
    }

    @Override
    public boolean existByName(String name) {
        return lines.stream()
                .anyMatch(line -> line.getName().equals(name));
    }

    @Override
    public boolean existByColor(String color) {
        return lines.stream()
                .anyMatch(line -> line.getColor().equals(color));
    }

    @Override
    public void update(Line line) {
        Line found = findById(line.getId()).get();
        lines.remove(found);
        lines.add(line);
    }

    @Override
    public void deleteById(Long id) {
        Line found = findById(id).get();
        lines.remove(found);
    }

    @Override
    public void deleteAll() {
        lines.clear();
    }
}
