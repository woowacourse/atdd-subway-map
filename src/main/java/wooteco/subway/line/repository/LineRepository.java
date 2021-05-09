package wooteco.subway.line.repository;

import wooteco.subway.line.Line;

import java.util.List;

public interface LineRepository {
    Line save(Line line);

    List<Line> findAll();

    boolean validateDuplicateName(String name);

    Line findById(Long id);

    void update(Line updatedLine);

    void delete(Long id);

    boolean validateUsableName(String oldName, String newName);
}
