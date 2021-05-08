package wooteco.subway.line.dao;

import wooteco.subway.line.domain.Line;

import java.util.List;

public interface LineDao {

    Line save(Line line);

    List<Line> findAll();

    Line findById(Long id);

    void delete(Long id);

    void update(Line line, Long id);

    void deleteAll();
}
