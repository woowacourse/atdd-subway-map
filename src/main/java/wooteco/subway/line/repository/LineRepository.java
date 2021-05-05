package wooteco.subway.line.repository;

import wooteco.subway.line.domain.Line;

import java.util.List;

public interface LineRepository {

    Line save(Line line);

    List<Line> findAll();

    Line findById(Long id);

    void delete(Long id);

    void update(Line line);

    void deleteAll();
}
