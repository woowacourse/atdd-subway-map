package wooteco.subway.section.repository;

import wooteco.subway.section.Section;
import wooteco.subway.station.Station;

import java.util.List;

public interface SectionDao {
    Section save(Long lineId, Section section);

    List<Section> findAllByLineId(Long lineId);

    Section findByUpStationId(Long lineId, Station upStationId);

    Section findByDownStationId(Long lineId, Station downStationId);

    void delete(Section section);

    void updateSectionToForward(Long lineId, Section newSection, int changedDistance);

    void updateSectionToBackward(Long lineId, Section newSection, int changedDistance);

    void deleteFirstSection(Long lineId, Long stationId);

    void deleteLastSection(Long lineId, Long stationId);
}
