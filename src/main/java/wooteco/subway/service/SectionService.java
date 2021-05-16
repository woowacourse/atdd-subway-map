package wooteco.subway.service;

import java.util.List;
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
import wooteco.subway.domain.line.StationsInLine;
import wooteco.subway.domain.section.Section;
import wooteco.subway.dao.SectionDao;

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

        StationsInLine stationsInLine = makeStationsInLine(section.getLineId());    //12

        stationsInLine.validStations(upStationId, downStationId);
        updateStation(section, upStationId, downStationId, stationsInLine);

        return sectionDao.save(section);
    }

    private void updateStation(Section section, long upStationId, long downStationId, StationsInLine stations) {
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

    // public int deleteSectionByStationId(long lineId, long stationId) {
    //     Optional<Section> optionalPreviousSection = sectionDao.findSectionBySameDownStation(lineId, stationId);
    //     Optional<Section> optionalNextSection = sectionDao.findSectionBySameUpStation(lineId, stationId);
    //
    //     if(makeStationsInLine(lineId).canNotDelete()) {
    //         throw new ImpossibleDeleteException();
    //     }
    //
    //     if (optionalPreviousSection.isPresent() && optionalNextSection.isPresent()) {
    //         Section nextSection = optionalNextSection.get();
    //         Section previousSection = optionalPreviousSection.get();
    //         previousSection.addDistance(nextSection);
    //         sectionDao.updateDownStation(previousSection, nextSection.getDownStationId());
    //         return deleteSection(nextSection);
    //     }
    //
    //     if (optionalPreviousSection.isPresent()) {
    //         return deleteSection(optionalPreviousSection.get());
    //     }
    //
    //     if (optionalNextSection.isPresent()) {
    //         return deleteSection(optionalNextSection.get());
    //     }
    //
    //     throw new NoSuchStationInLineException();
    // }

    public int deleteSectionByStationId(long lineId, long stationId) {
        StationsInLine stationsInLine = makeStationsInLine(lineId);

        if(stationsInLine.canNotDelete()) {
            throw new ImpossibleDeleteException();
        }

        List<Section> sectionsWithStation = stationsInLine.getSectionsWithStation(stationId);

        if(sectionsWithStation.size() == 0) {
            throw new NoSuchStationInLineException();
        }

        if (sectionsWithStation.size() == 2) {
            Section nextSection = stationsInLine.getDownSection(stationId);
            Section previousSection = stationsInLine.getUpSection(stationId);
            previousSection.addDistance(nextSection);
            sectionDao.updateDownStation(previousSection, nextSection.getDownStationId());
            return deleteSection(nextSection);
        }

        if (sectionsWithStation.size() == 1) {
            return deleteSection(sectionsWithStation.get(0));
        }

        throw new IllegalArgumentException();
    }

    private int deleteSection(Section nextSection) {
        return sectionDao.deleteSection(nextSection);
    }

    public StationsInLine makeStationsInLine(long id) {
        StationsInLine sectionsInLine = sectionDao.findSectionsByLineId(id);
        // if (sectionsInLine.getStations().isEmpty()) {
        //     throw new NoSuchStationInLineException();
        // }
        return sectionsInLine;
    }
}
