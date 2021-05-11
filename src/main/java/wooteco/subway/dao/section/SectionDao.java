package wooteco.subway.dao.section;

import java.util.List;
import wooteco.subway.domain.Section;

public interface SectionDao {

    Section save(Section section, Long lineId);

    List<Section> findAllByLineId(Long lineId);

    void update(Section section);

    void removeByStationId(Long lineId, Long stationId);
}
