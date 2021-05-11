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
import wooteco.subway.exception.NoSuchSectionException;
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
            .orElseThrow(NoSuchSectionException::new);

        previousSection.validateNewDistance(newSection);

        if (sectionDao.updateDownStation(previousSection, upStation) != 1) {
            throw new DuplicateSectionException();
        }
    }

    private void updateNextStation(Section newSection, Station upStation, Station downStation) {
        Section originSection = sectionDao.findSectionBySameUpStation(newSection.getLineId(), upStation)
            .orElseThrow(NoSuchSectionException::new);

        originSection.validateNewDistance(newSection);

        if (sectionDao.updateUpStation(originSection, downStation) != 1) {
            throw new DuplicateSectionException();
        }
    }

    public int deleteSectionByStationId(long lineId, long stationId) {
        Station station = stationService.showStation(stationId);
        Optional<Section> unKnownPreviousSection = sectionDao.findSectionBySameDownStation(lineId, station);
        Optional<Section> unknownNextSection = sectionDao.findSectionBySameUpStation(lineId, station);

        if (unKnownPreviousSection.isPresent() && unknownNextSection.isPresent()) {
            Section nextSection = unknownNextSection.get();
            Section previousSection = unKnownPreviousSection.get();
            previousSection.addDistance(nextSection);

            Station newDownStation = stationService.showStation(nextSection.getDownStationId());
            sectionDao.updateDownStation(previousSection, newDownStation);
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

    public List<Station> makeOrderedStations(long id) {
        try {
            return orderStations(sectionDao.findSectionsByLineId(id));
        } catch (DataAccessException e) {
            throw new NoSuchStationInLineException();
        }
    }

    private List<Station> orderStations(Map<Station, Station> sections) {
        List<Station> stations = new ArrayList<>();

        Station startStation = sections.keySet().stream()
            .filter(station -> !sections.containsValue(station))
            .findAny()
            .orElseThrow(IllegalInputException::new);
        stations.add(startStation);

        for (int i = 0; i < sections.size(); i++) {
            Station endStation = sections.get(startStation);
            stations.add(endStation);
            startStation = endStation;
        }

        return stations;
    }
}
