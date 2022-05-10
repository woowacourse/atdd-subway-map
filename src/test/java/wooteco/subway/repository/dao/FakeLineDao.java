package wooteco.subway.repository.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import wooteco.subway.domain.line.Line;

public class FakeLineDao implements LineDao {

    private final List<Line> lines = new ArrayList<>();
    private Long seq = 0L;

    @Override
    public Long save(Line line) {
        Line newLine = new Line(++seq, line.getName(), line.getColor());
        lines.add(newLine);
        return newLine.getId();
    }

    @Override
    public List<Line> findAll() {
        return lines;
    }

    @Override
    public Optional<Line> findById(Long id) {
        return lines.stream()
                .filter(line -> id.equals(line.getId()))
                .map(line -> new Line(line.getId(), line.getName(), line.getColor()))
                .findAny();
    }

    @Override
    public Boolean existsByName(String name) {
        return lines.stream()
                .anyMatch(line -> name.equals(line.getName()));
    }

    @Override
    public Boolean existsByColor(String color) {
        return lines.stream()
                .anyMatch(line -> color.equals(line.getColor()));
    }

    @Override
    public void update(Long id, String name, String color) {
        Line line = lines.stream()
                .filter(it -> id.equals(it.getId()))
                .findAny()
                .get();
        line.update(name, color);
    }

    @Override
    public void remove(Long id) {
        lines.remove(lines.stream()
                .filter(line -> id.equals(line.getId()))
                .findAny()
                .get());
    }
}
