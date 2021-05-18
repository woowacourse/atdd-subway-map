package wooteco.subway.line.domain.repository;

import wooteco.subway.line.domain.Line;
import wooteco.subway.line.domain.section.Section;

import java.util.List;

public interface LineRepository {

    Line saveLine(final Line line);

    Line findById(final Long id);

    List<Line> findAll();

    Line findLineSectionById(final Long id);

    void delete(final Long id);

    void update(final Line line);

    Section saveSection(final Section section);

    void addSection(Section section);

    void deleteSection(Long lineId, Long stationId);
}
