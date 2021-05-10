package wooteco.subway.dao;

import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;

import java.util.List;
import java.util.Optional;

public interface LineDao {

    Line save(Line line);

    List<Line> findAll();

    Optional<Line> findById(Long id);

    Optional<Line> findByName(String name);

    void update(Long id, Line line);

    void delete(Long id);

    void updateTopStationId(Section section);

    void updateBottomStationId(Section section);
}
