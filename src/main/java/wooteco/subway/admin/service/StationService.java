package wooteco.subway.admin.service;

import org.springframework.stereotype.Service;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.repository.StationRepository;

@Service
public class StationService {
    private final StationRepository stationRepository;

    public StationService(final StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public Long findStationId(final String name) {
        return stationRepository.findByName(name)
                .map(Station::getId)
                .orElseGet(()->null);
    }

    public Station save(final String stationName) {
        Station station = new Station(stationName);
        return stationRepository.save(station);
    }
}
