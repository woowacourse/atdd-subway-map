package wooteco.subway.dao;

import java.util.List;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

public interface SectionDao {

    Section save(Section section);

    List<Section> findAllByLineId(long lineId);

    boolean existByUpStationAndDownStation(Station upStation, Station downStation);
}
