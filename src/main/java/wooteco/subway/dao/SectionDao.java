package wooteco.subway.dao;

import java.util.Optional;
import wooteco.subway.domain.Section;

public interface SectionDao {

    Long insert(Section section);

    boolean isStationExist(long stationId);

    Optional<Section> findBy(Long lineId, Long upStationId, Long downStationId);

    Optional<Section> findByLineIdAndUpStationId(Long lineId, Long upStationId);
}
