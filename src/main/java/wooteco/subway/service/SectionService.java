package wooteco.subway.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.section.Sections;
import wooteco.subway.exception.duplicate.DuplicateSectionException;
import wooteco.subway.exception.illegal.IllegalInputException;
import wooteco.subway.exception.illegal.ImpossibleDeleteException;
import wooteco.subway.exception.nosuch.NoSuchSectionException;
import wooteco.subway.exception.nosuch.NoSuchStationInLineException;

@Service
@Transactional
public class SectionService {

    private final SectionDao sectionDao;

    @Autowired
    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public long createSection(Section section) {
        try {
            return sectionDao.save(section);
        } catch (DataAccessException e) {
            throw new IllegalInputException();
        }
    }

    public long addSection(Section section) {
        long upStationId = section.getUpStationId();
        long downStationId = section.getDownStationId();

        Sections sections = makeStationsInLine(section.getLineId());

        sections.validStations(upStationId, downStationId);
        updateStation(section, upStationId, downStationId, sections);

        return sectionDao.save(section);
    }

    private void updateStation(Section section, long upStationId, long downStationId, Sections stations) {
        if (stations.isEndStations(upStationId, downStationId)) {
            return;
        }

        if (stations.contains(upStationId)) {
            updateNextStation(section, upStationId, downStationId);
        }

        if (stations.contains(downStationId)) {
            updatePreviousStation(section, upStationId, downStationId);
        }
    }

    private void updatePreviousStation(Section newSection, long upStation, long downStation) {
        Section previousSection = sectionDao.findSectionBySameDownStation(newSection.getLineId(), downStation)
            .orElseThrow(NoSuchSectionException::new);

        previousSection.updateValidDistance(newSection);

        if (sectionDao.updateDownStation(previousSection, upStation) != 1) {
            throw new DuplicateSectionException();
        }
    }

    private void updateNextStation(Section newSection, long upStation, long downStation) {
        Section originSection = sectionDao.findSectionBySameUpStation(newSection.getLineId(), upStation)
            .orElseThrow(NoSuchSectionException::new);

        originSection.updateValidDistance(newSection);

        if (sectionDao.updateUpStation(originSection, downStation) != 1) {
            throw new DuplicateSectionException();
        }
    }

    public int deleteSectionByStationId(long lineId, long stationId) {
        Sections sections = makeStationsInLine(lineId);

        if (sections.canNotDelete()) {
            throw new ImpossibleDeleteException();
        }

        List<Section> sectionsWithStation = sections.getSectionsWithStationId(stationId);
        return deleteSection(stationId, sections, sectionsWithStation);
    }

    private Integer deleteSection(long stationId, Sections sections, List<Section> sectionsWithStationId) {
        if (sectionsWithStationId.size() == 0) {
            throw new NoSuchStationInLineException();
        }

        if (sectionsWithStationId.size() == 2) {
            Section nextSection = sections.getDownSection(stationId);
            Section previousSection = sections.getUpSection(stationId);
            previousSection.addDistance(nextSection);
            sectionDao.updateDownStation(previousSection, nextSection.getDownStationId());
            return sectionDao.deleteSection(nextSection);
        }

        if (sectionsWithStationId.size() == 1) {
            return sectionDao.deleteSection(sectionsWithStationId.get(0));
        }

        throw new IllegalArgumentException();
    }

    public Sections makeStationsInLine(long id) {
        Sections sectionsInLine = sectionDao.findSectionsByLineId(id);
        if (sectionsInLine.getSections().isEmpty()) {
            throw new NoSuchStationInLineException();
        }
        return sectionsInLine;
    }
}
