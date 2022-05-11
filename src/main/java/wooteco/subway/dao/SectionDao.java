package wooteco.subway.dao;

import java.util.List;
import wooteco.subway.domain.Section;

public interface SectionDao {
    Section save(Section section, Long lineId);

    List<Section> findByLine(Long lineId);

    void deleteByLine(Long lineId);
}
