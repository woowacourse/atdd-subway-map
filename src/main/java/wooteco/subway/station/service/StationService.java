package wooteco.subway.station.service;

import java.util.List;
import org.springframework.stereotype.Service;
import wooteco.subway.exception.DuplicateStationNameException;
import wooteco.subway.exception.NotExistStationException;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.repository.StationRepository;

@Service
public class StationService {

    private final StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public Station createStation(Station station) {
        if (stationRepository.isExistName(station)) {
            throw new DuplicateStationNameException();
        }
        return stationRepository.save(station);
    }

    public List<Station> showStations() {
        List<Station> stations = stationRepository.findAll();
        if (stations.isEmpty()) {
            throw new NotExistStationException();
        }
        return stations;
    }

    public void deleteStation(Long id) {
        if (stationRepository.delete(id) == 0) {
            throw new NotExistStationException();
        }
    }

}
