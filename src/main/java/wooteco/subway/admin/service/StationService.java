package wooteco.subway.admin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.repository.StationRepository;

import java.util.Optional;

@Service
public class StationService {
    @Autowired
    private StationRepository stationRepository;

    public Long findStationId(final String name) {
        return Optional.ofNullable(name)
                .flatMap(stationName -> stationRepository.findByName(stationName))
                .map(Station::getId)
                .orElseGet(()->null);
    }

    public Station save(final String stationName) {
        Station station = new Station(stationName);
        return stationRepository.save(station);
    }
}
