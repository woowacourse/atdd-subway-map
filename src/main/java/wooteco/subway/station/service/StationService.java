package wooteco.subway.station.service;

import org.springframework.stereotype.Service;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.domain.StationRepository;

import java.util.List;

@Service
public class StationService {

    private final StationRepository stationRepository;

    public StationService(final StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public Station createStation(Station station) {
        return stationRepository.save(station);
    }

    public List<Station> findAll() {
        return stationRepository.findAll();
    }

    public int delete(Long id) {
        return stationRepository.delete(id);
    }

}
