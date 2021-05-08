package wooteco.subway.station.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.repository.StationRepository;

import java.util.List;

@Service
@Transactional
public class StationService {
    private final StationRepository stationRepository;

    public StationService(final StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public Station createStation(final Station station) {
        if (stationRepository.doesNameExist(station)) {
            throw new DuplicateStationNameException();
        }
        return stationRepository.save(station);
    }

    public List<Station> findAll() {
        return stationRepository.getStations();
    }

    public void delete(final Long id) {
        if (stationRepository.doesIdNotExist(id)) {
            throw new NoSuchStationException();
        }
        stationRepository.deleteById(id);
    }
}
