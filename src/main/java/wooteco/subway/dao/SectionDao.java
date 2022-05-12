package wooteco.subway.dao;

import java.util.List;
import wooteco.subway.domain.Section;

public interface SectionDao {
    Section save(Long lineId, Section section);

    List<Section> findByLineId(long lineId);

    void batchUpdate(Long lineId, List<Section> sections);

    void deleteSectionsByLineId(Long lineId);

    void deleteById(Long sectionId, Long lineId);
}
