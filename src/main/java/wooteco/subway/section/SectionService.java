package wooteco.subway.section;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import wooteco.subway.exception.DuplicateSectionException;
import wooteco.subway.exception.IllegalInputException;
import wooteco.subway.exception.ImpossibleDeleteException;
import wooteco.subway.exception.NoSuchStationInLineException;
import wooteco.subway.line.StationsInLine;
import wooteco.subway.station.Station;
import wooteco.subway.station.StationService;

@Service
@Transactional
public class SectionService {

    private final SectionDao sectionDao;

    private final StationService stationService;

    @Autowired
    public SectionService(SectionDao sectionDao, StationService stationService) {
        this.sectionDao = sectionDao;
        this.stationService = stationService;
    }

    public long createSection(Section section) {
        try {
            return sectionDao.save(section);
        } catch (DataAccessException e) {
            throw new IllegalInputException();
        }
    }

    public long addSection(Section section) {
        Station upStation = stationService.showStation(section.getUpStationId());
        Station downStation = stationService.showStation(section.getDownStationId());
        List<Station> orderedStations = makeOrderedStations(section.getLineId());
        StationsInLine stations = new StationsInLine(orderedStations);

        stations.validStations(upStation, downStation);
        checkSavingOptions(section, upStation, downStation, stations);

        return sectionDao.save(section);
    }

    private void checkSavingOptions(Section section, Station upStation, Station downStation, StationsInLine stations) {
        if (stations.isEndStations(upStation, downStation)) {
            return;
        }

        if (stations.contains(upStation)) {
            updateNextStation(section, upStation, downStation);
        }

        if (stations.contains(downStation)) {
            updatePreviousStation(section, upStation, downStation);
        }
    }

    private void updatePreviousStation(Section newSection, Station upStation, Station downStation) {
        Section previousSection = sectionDao.findSectionBySameDownStation(newSection.getLineId(), downStation)
            .orElseThrow(IllegalInputException::new);

        previousSection.validateDistance(newSection);

        if (sectionDao.updateDownStation(newSection, upStation) != 1) {
            throw new DuplicateSectionException();
        }
    }

    private void updateNextStation(Section newSection, Station upStation, Station downStation) {
        Section originSection = sectionDao.findSectionBySameUpStation(newSection.getLineId(), upStation)
            .orElseThrow(IllegalInputException::new);

        originSection.validateDistance(newSection);;

        if (sectionDao.updateUpStation(newSection, downStation) != 1) {
            throw new DuplicateSectionException();
        }
    }

    public int deleteSection(long lineId, long stationId) {
        Station station = stationService.showStation(stationId);

        Optional<Section> previousSection = sectionDao.findSectionBySameDownStation(lineId, station);
        Optional<Section> nextSection = sectionDao.findSectionBySameUpStation(lineId, station);

        if (previousSection.isPresent() && nextSection.isPresent()) {
            Station newDownStation = stationService.showStation(nextSection.get().getDownStationId());
            sectionDao.updateDownStation(previousSection.get(), newDownStation);
            return checkPossibleDelete(nextSection.get());
        }

        if (previousSection.isPresent()) {
            return checkPossibleDelete(previousSection.get());
        }

        if (nextSection.isPresent()) {
            return checkPossibleDelete(nextSection.get());
        }

        throw new NoSuchStationInLineException();
    }

    private int checkPossibleDelete(Section nextSection) {
        if (sectionDao.findSectionsByLineId(nextSection.getLineId()).size() == 1) {
            throw new ImpossibleDeleteException();
        }
        return sectionDao.deleteSection(nextSection);
    }


    public List<Station> makeOrderedStations(long id) {
        try {
            long startStationId = sectionDao.findStartStationIdByLineId(id);
            long endStationId = sectionDao.findEndStationIdByLineId(id);
            Map<Long, Long> sections = sectionDao.findSectionsByLineId(id);
            return orderStations(startStationId, endStationId, sections);
        } catch (DataAccessException e) {
            throw new NoSuchStationInLineException();
        }
    }

    public List<Station> orderStations(long startStationId, long endStationId, Map<Long, Long> sections) {
        List<Station> stations = new ArrayList<>();
        long sectionStartId = startStationId;
        stations.add(stationService.showStation(sectionStartId));

        while (sectionStartId != endStationId) {
            long sectionEndId = sections.get(sectionStartId);
            stations.add(stationService.showStation(sectionEndId));
            sectionStartId = sectionEndId;
        }

        return stations;
    }
}
