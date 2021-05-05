package wooteco.subway.station.service;

import org.springframework.stereotype.Service;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.repository.StationRepository;

import java.util.List;

@Service
public class StationService {
    private final StationRepository stationRepository;

    public StationService(final StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public Station createStation(final Station station) {
        if (stationRepository.isExist(station)) {
            throw new IllegalArgumentException("이미 존재하는 Station 입니다");
        }
        return stationRepository.save(station);
    }

    public List<Station> findAll() {
        return stationRepository.findAll();
    }

    public void delete(final Long id) {
        stationRepository.deleteById(id);
    }
}
