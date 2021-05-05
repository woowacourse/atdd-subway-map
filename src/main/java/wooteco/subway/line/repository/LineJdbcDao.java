package wooteco.subway.line.repository;

import wooteco.subway.line.Line;

import java.util.List;
import java.util.Optional;

public class LineJdbcDao implements LineRepository {
    @Override
    public Line save(Line line) {
        return null;
    }

    @Override
    public List<Line> findAll() {
        return null;
    }

    @Override
    public Optional<Line> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public void update(Line currentLine, Line updatedLine) {

    }

    @Override
    public void delete(Line line) {

    }
}
