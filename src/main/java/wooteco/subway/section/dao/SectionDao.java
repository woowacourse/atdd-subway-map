package wooteco.subway.section.dao;

import org.springframework.stereotype.Repository;
import wooteco.subway.section.domain.Section;

import java.util.List;

@Repository
public interface SectionDao {
    void save(Section section);

    List<Section> findAllByLineId(Long lineId);

    void updateByDownStationId(Long lineId, Long downStationId, Long upStationId, int distance);

    void updateByUpStationId(Long lineId, Long upStationId, Long downStationId, int distance);

    void delete(Long id);
}
