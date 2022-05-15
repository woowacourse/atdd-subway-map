package wooteco.subway.dao;

import java.util.List;
import wooteco.subway.domain.Section;

public interface SectionDao<T> {
    T save(T Section);

    int deleteSectionById(List<Long> ids);

    List<Section> findAllByLineId(Long lineId);

    int updateUpStationSection(Long lineId, Long originUpStationId, Long upStationId, int distance);

    int countByLineId(Long lineId);

    List<Section> findAllByLineIdAndStationId(Long lineId, Long stationId);

    int updateDownStationSection(Long lineId, Long id, Long downStationId, int distance);
}
