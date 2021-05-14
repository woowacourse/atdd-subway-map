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
    private final JdbcStationDao stationDao;
    private final JdbcLineDao lineDao;
    private final JdbcSectionDao sectionDao;

    @Transactional
    public Section create(Section newSection, Long lineId) {
        Sections sections = findAllByLineId(lineId);

        Section modified = sections.modifyRelated(newSection);
        sections.add(newSection);

        sectionDao.updateModified(modified);
        return sectionDao.create(newSection, lineId);
    }

    @Transactional
    public Section createInitial(Section section, Long lineId) {
        return sectionDao.create(section, lineId);
    }

    @Transactional
    public void remove(Long lineId, Long stationId) {
        validateExistLine(lineId);
        validateExistStation(stationId);
        validateIsLastRemainedSection(lineId);

        Station station = stationDao.findById(stationId);
        Sections sections = findAllByLineId(lineId);

        List<Section> removed = sections.removeRelated(station);
        Section modified = sections.reflectRemoved(removed, station);

        for (Section section : removed) {
            Long upStationId = section.getUpStation().getId();
            Long downStationId = section.getDownStation().getId();
            sectionDao.remove(lineId, upStationId, downStationId);
        }
        sectionDao.create(modified, lineId);
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
