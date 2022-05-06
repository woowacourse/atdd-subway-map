package wooteco.subway.dao;

import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

public interface SectionDao {

    Section save(Section section);

    boolean existByUpStationAndDownStation(Station upStation, Station downStation);
}
