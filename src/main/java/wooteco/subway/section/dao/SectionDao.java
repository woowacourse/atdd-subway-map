package wooteco.subway.section.dao;

import wooteco.subway.section.domain.Section;
import wooteco.subway.section.domain.Sections;
import wooteco.subway.station.domain.Station;

public interface SectionDao {

    Section save(Section section);

    Sections findByLineId(Long lineId);

    void update(Section updateSection);

    void delete(Long lineId, Station station);

}
