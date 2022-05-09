package wooteco.subway.dao;

import wooteco.subway.domain.Section;

public interface SectionDao {

    Long save(Section section);

    boolean existByLineIdAndStationId(long lineId, long stationId);
}
