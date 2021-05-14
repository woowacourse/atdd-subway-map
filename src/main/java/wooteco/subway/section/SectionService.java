package wooteco.subway.section;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.line.LineNotFoundException;
import wooteco.subway.exception.section.SectionLastRemainedException;
import wooteco.subway.exception.station.StationNotFoundException;
import wooteco.subway.line.dao.JdbcLineDao;
import wooteco.subway.section.dao.JdbcSectionDao;
import wooteco.subway.section.dao.SectionTable;
import wooteco.subway.station.dao.JdbcStationDao;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SectionService {

    private static final int WHEN_BETWEEN_SECTIONS = 2;
    private final JdbcStationDao stationDao;
    private final JdbcLineDao lineDao;
    private final JdbcSectionDao sectionDao;

    @Transactional
    public Section create(Section newSection, Long lineId) {
        Sections sections = findAllByLineId(lineId);
        Section modifiedSection = sections.addAndThenGetModifiedAdjacent(newSection);

        sectionDao.saveModified(modifiedSection, lineId);
        return sectionDao.create(newSection, lineId);
    }

    @Transactional
    public void remove(Long lineId, Long stationId) {
        validateExistLine(lineId);
        validateExistStation(stationId);
        validateIsLastRemainedSection(lineId);

        Station station = stationDao.findById(stationId);

        List<SectionTable> sectionTables = sectionDao.findAdjacentByStationId1111(lineId, stationId);
        List<Section> effectiveSections = convertToSections(sectionTables);

        for (Section section : effectiveSections) {
            Long upStationId = section.getUpStation().getId();
            Long downStationId = section.getDownStation().getId();
            sectionDao.remove(lineId, upStationId, downStationId);
        }

        if (effectiveSections.size() == WHEN_BETWEEN_SECTIONS) {
            Section section = Sections.create(effectiveSections).removeStationInBetween(station);
            sectionDao.insertSection(section, lineId);
        }
    }

    private void validateIsLastRemainedSection(Long lineId) {
        if (findAllByLineId(lineId).hasSize(1)) {
            throw new SectionLastRemainedException();
        }
    }

    public Sections findAllByLineId(Long lineId) {
        List<SectionTable> sectionTables = sectionDao.findAllByLineId(lineId);
        List<Section> sections = convertToSections(sectionTables);
        return Sections.create(sections);
    }

    private List<Section> convertToSections(List<SectionTable> sectionTables) {
        List<Section> sections = new ArrayList<>();
        for (SectionTable sectionTable : sectionTables) {
            Station upStation = stationDao.findById(sectionTable.getUpStationId());
            Station downStation = stationDao.findById(sectionTable.getDownStationId());
            sections.add(Section.create(sectionTable.getId(), upStation, downStation, sectionTable.getDistance()));
        }
        return sections;
    }

    private void validateExistStation(Long stationId) {
        if (!stationDao.existById(stationId)) {
            throw new StationNotFoundException();
        }
    }

    private void validateExistLine(Long lineId) {
        if (!lineDao.existById(lineId)) {
            throw new LineNotFoundException();
        }
    }
}
