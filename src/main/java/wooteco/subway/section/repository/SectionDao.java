package wooteco.subway.section.repository;

import wooteco.subway.line.Line;
import wooteco.subway.section.Section;
import wooteco.subway.station.Station;

import java.util.List;

public interface SectionDao {
    Section save(Section section);

    List<Section> findAllByLineId(Long lineId);

    Section findByUpStationId(Long lineId, Station upStationId);

    Section findByDownStationId(Long lineId, Station downStationId);

    void delete(Section section);

    void updateSectionToForward(Section newSection, int changedDistance);

    void updateSectionToBackward(Section newSection, int changedDistance);

    void deleteByLineId(Line line);
}
