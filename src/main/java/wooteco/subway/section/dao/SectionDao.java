package wooteco.subway.section.dao;

import java.util.List;
import wooteco.subway.section.Section;

public interface SectionDao {

    Section save(Section section);

    List<Section> findSectionsByLineId(Long lineId);

    List<Section> findById(Long lineId, Long id);

    void deleteById(Long lineId, Long id);

    void deleteAllById(Long id);

    void deleteBySectionId(Long id);
}
