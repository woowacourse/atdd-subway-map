package wooteco.subway.line.dao;

import wooteco.subway.line.Line;

import java.util.List;

public interface LineDao {
    Line save(Line line);

    List<Line> findAll();

    Line findById(Long id);

    void update(Line line);

    void delete(Long id);

    boolean existByNameAndNotInOriginalName(String name, String originalName);
}
