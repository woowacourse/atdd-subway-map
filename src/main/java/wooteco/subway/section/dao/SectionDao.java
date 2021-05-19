package wooteco.subway.section.dao;

import java.util.List;
import wooteco.subway.section.Section;
import wooteco.subway.section.dto.SectionDto;

public interface SectionDao {

    Section save(Long lineId, Section section);

    int countById(Long lineId);

    List<SectionDto> findSectionsByLineId(Long lineId);

    List<SectionDto> findById(Long lineId, Long id);

    void deleteByStationId(Long lineId, Long id);

    void deleteAllById(Long id);

    void deleteById(Long id);
}
