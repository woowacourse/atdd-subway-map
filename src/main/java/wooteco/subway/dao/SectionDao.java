package wooteco.subway.dao;

import wooteco.subway.domain.Section;

import java.util.List;

public interface SectionDao {

    Section create(Section section);

    void delete(Long id);

    boolean existById(Long id);

    List<Section> findAllByLineId(Long lineId);

    void updateUpStationId(Long id, Long downStationId, int calculateDistance);

    void updateDownStationId(Long id, Long upStationId, int calculateDistance);
}
