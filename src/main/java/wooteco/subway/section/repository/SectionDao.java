package wooteco.subway.section.repository;

import wooteco.subway.line.Line;
import wooteco.subway.section.Section;

import java.util.List;

public interface SectionDao {
    Section save(Section section);

    List<Section> findAllByLineId(Long lineId);

    void delete(Section section);

    void deleteByLineId(Line line);
}
