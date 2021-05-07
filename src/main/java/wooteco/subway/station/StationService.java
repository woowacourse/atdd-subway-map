package wooteco.subway.station;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wooteco.subway.station.exception.DuplicateStationNameException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationService {
    private final StationRepository stationRepository;

    @Autowired
    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public StationResponse create(String name) {
        Station station = new Station(name);
        validateDuplicateStationName(name);
        Station newStation = stationRepository.save(station);
        return new StationResponse(newStation.getId(), newStation.getName());
    }

    private void validateDuplicateStationName(String name) {
        this.stationRepository.findByName(name).ifPresent(line -> {
            throw new DuplicateStationNameException(name);
        });
    }

    public List<StationResponse> findAll() {
        return stationRepository.findAll().stream()
                .map(it -> new StationResponse(it.getId(), it.getName()))
                .collect(Collectors.toList());
    }

    public void delete(Long id) {
        stationRepository.delete(id);
    }
}
