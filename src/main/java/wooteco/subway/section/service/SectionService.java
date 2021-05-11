package wooteco.subway.section.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import wooteco.subway.exception.duplicate.DuplicateSectionException;
import wooteco.subway.exception.illegal.IllegalInputException;
import wooteco.subway.exception.illegal.ImpossibleDeleteException;
import wooteco.subway.exception.nosuch.NoSuchSectionException;
import wooteco.subway.exception.nosuch.NoSuchStationInLineException;
import wooteco.subway.line.StationsInLine;
import wooteco.subway.section.Section;
import wooteco.subway.section.dao.SectionDao;
import wooteco.subway.station.Station;

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

        StationsInLine stationsInLine = makeStationsInLine(section.getLineId());

        stationsInLine.validStations(upStationId, downStationId);
        checkSavingOptions(section, upStationId, downStationId, stationsInLine);

        return sectionDao.save(section);
    }

    private void checkSavingOptions(Section section, long upStationId, long downStationId, StationsInLine stations) {
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

        previousSection.validateNewDistance(newSection);

        if (sectionDao.updateDownStation(previousSection, upStation) != 1) {
            throw new DuplicateSectionException();
        }
    }

    private void updateNextStation(Section newSection, long upStation, long downStation) {
        Section originSection = sectionDao.findSectionBySameUpStation(newSection.getLineId(), upStation)
            .orElseThrow(NoSuchSectionException::new);

        originSection.validateNewDistance(newSection);

        if (sectionDao.updateUpStation(originSection, downStation) != 1) {
            throw new DuplicateSectionException();
        }
    }

    public int deleteSectionByStationId(long lineId, long stationId) {
        Optional<Section> unKnownPreviousSection = sectionDao.findSectionBySameDownStation(lineId, stationId);
        Optional<Section> unknownNextSection = sectionDao.findSectionBySameUpStation(lineId, stationId);

        if (unKnownPreviousSection.isPresent() && unknownNextSection.isPresent()) {
            Section nextSection = unknownNextSection.get();
            Section previousSection = unKnownPreviousSection.get();
            previousSection.addDistance(nextSection);
            sectionDao.updateDownStation(previousSection, nextSection.getDownStationId());
            return deleteSection(nextSection);
        }

        if (unKnownPreviousSection.isPresent()) {
            return deleteSection(unKnownPreviousSection.get());
        }

        if (unknownNextSection.isPresent()) {
            return deleteSection(unknownNextSection.get());
        }

        throw new NoSuchStationInLineException();
    }

    private int deleteSection(Section nextSection) {
        if (sectionDao.findSectionsByLineId(nextSection.getLineId()).size() == 1) {
            throw new ImpossibleDeleteException();
        }
        return sectionDao.deleteSection(nextSection);
    }

    public StationsInLine makeStationsInLine(long id) {
        Map<Station, Station> sectionsInLine = sectionDao.findSectionsByLineId(id);
        if (sectionsInLine.isEmpty()) {
            throw new NoSuchStationInLineException();
        }
        return StationsInLine.from(sectionsInLine);
    }
}
