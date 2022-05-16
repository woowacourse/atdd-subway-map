package wooteco.subway.dao;

import wooteco.subway.domain.Line;

import java.util.List;

public interface LineDao {

    Line save(Line line);

    boolean isExistById(Long id);

    boolean isExistByName(String name);

    Line findById(Long id);

    List<Line> findAll();

    int update(Long id, Line line);

    int delete(Long id);
}
