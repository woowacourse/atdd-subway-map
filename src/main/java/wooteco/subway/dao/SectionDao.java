package wooteco.subway.dao;

import java.util.List;
import wooteco.subway.domain.Section;

public interface SectionDao {

    Long save(Section section);

    void updateLineOrder(Long lineId, Long lineOrder);

    boolean existByLineId(Long lineId);

    List<Section> findAllByLineId(Long lineId);

    void deleteById(Long id);

    List<Section> findByLineIdAndStationId(long lineId, long stationId);

    void updateLineOrderByDec(long lineId, long lineOrder);
}
