package wooteco.subway.section.dao;

import java.util.List;
import wooteco.subway.section.Section;
import wooteco.subway.section.dto.SectionResponse;

public interface SectionDao {

    Section save(Long lineId, Section section);

    List<SectionResponse> findSectionsByLineId(Long lineId);

    List<SectionResponse> findById(Long lineId, Long id);

    void deleteByStationId(Long lineId, Long id);

    void deleteAllById(Long id);

    void deleteById(Long id);
}
