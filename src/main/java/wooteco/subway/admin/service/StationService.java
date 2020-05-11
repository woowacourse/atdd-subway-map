package wooteco.subway.admin.service;

import java.util.List;

import org.springframework.stereotype.Service;

import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.repository.StationRepository;

@Service
public class StationService {

    StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public Station addStation(final Station station) {
        return stationRepository.save(station);
    }

    public List<Station> showStations() {
        return stationRepository.findAll();
    }

    public Station findStationByName(final String stationName) {
        return stationRepository.findByName(stationName);
    }

    public void removeStation(final Long id) {
        stationRepository.deleteById(id);
    }
}
