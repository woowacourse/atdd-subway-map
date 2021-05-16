package wooteco.subway.line.repository;

import wooteco.subway.line.Line;

import java.util.List;

public interface LineDao {
    Line save(Line line);

    List<Line> findAll();

    Line findById(Long id);

    void update(Line updatedLine);

    void delete(Long id);

    boolean isExistingName(String name);

    boolean existNewNameExceptCurrentName(String newName, String oldName);
}
