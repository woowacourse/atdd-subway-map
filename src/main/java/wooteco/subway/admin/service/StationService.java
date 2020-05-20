package wooteco.subway.admin.service;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.StationResponse;
import wooteco.subway.admin.exceptions.DuplicateStationException;
import wooteco.subway.admin.repository.StationRepository;

@Service
public class StationService {

    private StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public Station save(Station station) {
        List<StationResponse> persistStations = StationResponse.listOf(stationRepository.findAll());
        boolean hasDuplicateName = persistStations.stream()
            .anyMatch(existStation -> existStation.getName().equals(station.getName()));
        if (hasDuplicateName) {
            throw new DuplicateStationException(station.getName());
        }
        return stationRepository.save(station);
    }

    public Set<Station> findAll() {
        return stationRepository.findAll();
    }

    public void removeStation(Long id) {
        stationRepository.deleteById(id);
    }
}
