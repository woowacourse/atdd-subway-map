package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.StationRepository;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.exception.DuplicateStationNameException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationService {
    private final StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public StationResponse createStation(String name) {
        Station station = new Station(name);
        validateDuplicateStationName(name);

        Station newStation = stationRepository.save(station);
        return new StationResponse(newStation.getId(), newStation.getName());
    }

    private void validateDuplicateStationName(String name) {
        stationRepository.findByName(name).ifPresent(line -> {
            throw new DuplicateStationNameException(name);
        });
    }

    public List<StationResponse> findAllStations() {
        List<Station> all = stationRepository.findAll();
        return all.stream()
                .map(it -> new StationResponse(it.getId(), it.getName()))
                .collect(Collectors.toList());
    }

    public void deleteStation(Long id) {
        stationRepository.delete(id);
    }
}
