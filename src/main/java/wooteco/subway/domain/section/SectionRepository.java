package wooteco.subway.domain.section;

import java.util.List;

import wooteco.subway.domain.station.Station;

public interface SectionRepository {

    List<Section> findSectionsByLineId(Long lineId);

    void updateSections(Long lineId, Sections sections);

    Station findStationById(Long stationId);
}
