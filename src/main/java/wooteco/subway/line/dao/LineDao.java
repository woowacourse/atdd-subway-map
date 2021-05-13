package wooteco.subway.line.dao;

import wooteco.subway.line.Line;

import java.util.List;

public interface LineDao {
    Line save(Line line);

    List<Line> findAll();

    Line findById(Long id);

//    Line findByName(String name);

    void update(Line updatedLine);

    void delete(Long id);

//    String findByNameAndNotInOriginalName(String name, String originalName);

    boolean existByName(String name);

    boolean existByNameAndNotInOriginalName(String name, String originalName);
}
