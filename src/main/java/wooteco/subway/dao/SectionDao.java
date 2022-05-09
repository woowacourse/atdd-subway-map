package wooteco.subway.dao;

import java.util.Optional;
import wooteco.subway.domain.Section;

public interface SectionDao {

    Long save(Section section);

    boolean existByLineIdAndStationId(long lineId, long stationId);

    Optional<Long> findIdByLineIdAndDownStationId(long lineId, long stationId);

    Optional<Long> findIdByLineIdAndUpStationId(long lineId, long stationId);
}
