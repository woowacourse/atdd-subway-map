package wooteco.subway.section;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import wooteco.subway.exception.BothStationInLineException;
import wooteco.subway.exception.BothStationNotInLineException;
import wooteco.subway.exception.DuplicateSectionException;
import wooteco.subway.exception.IllegalInputException;
import wooteco.subway.exception.ImpossibleDistanceException;
import wooteco.subway.exception.NoSuchStationInLineException;
import wooteco.subway.line.Line;
import wooteco.subway.line.LineService;
import wooteco.subway.station.Station;
import wooteco.subway.station.StationService;

@Service
public class SectionService {
    private final SectionDao sectionDao;
    private final LineService lineService;
    private final StationService stationService;

    @Autowired
    public SectionService(SectionDao sectionDao, LineService lineService, StationService stationService) {
        this.sectionDao = sectionDao;
        this.lineService = lineService;
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
        Line line = lineService.showLine(section.getLineId());
        List<Station> orderedStations = line.getStations();

        if (orderedStations.containsAll(Arrays.asList(upStation, downStation))) {
            throw new BothStationInLineException();
        }

        if (orderedStations.get(0).equals(downStation) || orderedStations.get(orderedStations.size() - 1)
            .equals(upStation)) {
            return sectionDao.save(section);
        }

        if (orderedStations.contains(upStation)) {
            updateNextStation(section, upStation, downStation);
            return sectionDao.save(section);
        }

        if (orderedStations.contains(downStation)) {
            updatePreviousStation(section, upStation, downStation);
            return sectionDao.save(section);
        }

        throw new BothStationNotInLineException();

    }

    private void updatePreviousStation(Section section, Station upStation, Station downStation) {
        Section previousSection = sectionDao.findSectionBySameDownStation(section.getLineId(), downStation)
            .orElseThrow(IllegalInputException::new);
        int originDistance = previousSection.getDistance();
        int newDistance = section.getDistance();

        validateDistance(originDistance, newDistance);

        if (sectionDao.updateDownStation(section, upStation) != 1) {
            throw new DuplicateSectionException();
        }
    }

    private void updateNextStation(Section section, Station upStation, Station downStation) {
        Section nextSection = sectionDao.findSectionBySameUpStation(section.getLineId(), upStation)
            .orElseThrow(IllegalInputException::new);
        int originDistance = nextSection.getDistance();
        int newDistance = section.getDistance();

        validateDistance(originDistance, newDistance);

        if (sectionDao.updateUpStation(section, downStation) != 1) {
            throw new DuplicateSectionException();
        }
    }

    private void validateDistance(int originDistance, int newDistance) {
        if (originDistance - newDistance < 0) {
            throw new ImpossibleDistanceException();
        }
    }

    public int deleteSection(long lineId, long stationId) {
        Station station = stationService.showStation(stationId);

        Optional<Section> previousSection = sectionDao.findSectionBySameDownStation(lineId, station);
        Optional<Section> nextSection = sectionDao.findSectionBySameUpStation(lineId, station);

        if(previousSection.isPresent() && nextSection.isPresent()) {
            Station newDownStation = stationService.showStation(nextSection.get().getDownStationId());
            sectionDao.updateDownStation(previousSection.get(), newDownStation);
            return sectionDao.deleteSection(nextSection.get());
        }

        if(previousSection.isPresent()) {
            return sectionDao.deleteSection(previousSection.get());
        }

        if(nextSection.isPresent()) {
            return sectionDao.deleteSection(nextSection.get());
        }

        throw new NoSuchStationInLineException();
    }

}
