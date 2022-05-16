package wooteco.subway.dao;

import java.util.List;
import wooteco.subway.domain.Section;

public interface SectionDao {

    Long save(Section section);

    void updateLineOrderByInc(long lineId, Long lineOrder);

    boolean existByLineId(long lineId);

    List<Section> findAllByLineId(long lineId);

    void deleteById(Long id);

    List<Section> findByLineIdAndStationId(long lineId, long stationId);

    void updateLineOrderByDec(long lineId, Long lineOrder);
}
