package wooteco.subway.dao;

import java.util.Optional;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;

public interface SectionDao {

    Long insert(Section section);

    boolean existStation(long stationId);

    Sections findAllByLineId(Long lineId);

    Optional<Section> findBy(Long lineId, Long upStationId, Long downStationId);

    Optional<Section> findByLineIdAndUpStationId(Long lineId, Long upStationId);

    Optional<Section> findByLineIdAndDownStationId(Long lineId, Long downStationId);

    Integer deleteById(Long id);
}
