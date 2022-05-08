package wooteco.subway.dao;

import wooteco.subway.domain.Section;

public interface SectionDao {

    Long insert(Section section);

    boolean isStationExist(long stationId);
}
