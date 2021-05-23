package wooteco.subway.station.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.exception.DuplicateStationNameException;
import wooteco.subway.exception.NotExistStationException;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.dto.StationRequest;
import wooteco.subway.station.repository.StationRepository;

@Transactional
@Service
public class StationService {

    private final StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public Station createStation(StationRequest stationRequest) {
        Station station = new Station(stationRequest.getName());
        if (stationRepository.isExistName(station)) {
            throw new DuplicateStationNameException();
        }
        return stationRepository.save(station);
    }

    @Transactional(readOnly = true)
    public Station findById(Long id) {
        Optional<Station> station = stationRepository.findById(id);
        if (station.isPresent()) {
            return station.get();
        }
        throw new NotExistStationException();
    }

    public List<Station> findStations() {
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
