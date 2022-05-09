package wooteco.subway.dao;

import java.util.Optional;
import wooteco.subway.domain.Section;

public interface SectionDao {

    Long save(Section section);

    boolean existByLineIdAndStationId(Long lineId, Long stationId);

    Optional<Long> findIdByLineIdAndDownStationId(Long lineId, Long stationId);

    Optional<Long> findIdByLineIdAndUpStationId(Long lineId, Long stationId);

    int findDistanceById(Long id);

    Long findLineOrderById(Long id);

    void updateLineOrder(Long lineId, Long lineOrder);

    boolean existByLineId(Long lineId);
}
