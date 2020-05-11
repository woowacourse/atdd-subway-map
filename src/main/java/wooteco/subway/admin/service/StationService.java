package wooteco.subway.admin.service;

import org.springframework.stereotype.Service;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.repository.StationRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StationService {
    private StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public Station save(String name) {
        Station station = new Station(name);
        return stationRepository.save(station);
    }

    public Iterable<Station> findAllOfStations() {
        return stationRepository.findAll();
    }

    public void deleteStationById(Long id) {
        Station station = stationRepository.findById(id)
                .orElseThrow(IllegalArgumentException::new);
        stationRepository.delete(station);

    }

    public Station findByName(String name) {
        return stationRepository.findByName(name);
    }

    public Set<Station> findAllOf(Line line) {
        List<Long> stationIds = line.getStations().stream()
                .map(LineStation::getStationId)
                .collect(Collectors.toList());

        return stationRepository.findAllById(stationIds);
    }
}
