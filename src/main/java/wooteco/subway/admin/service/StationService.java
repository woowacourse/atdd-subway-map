package wooteco.subway.admin.service;

import org.springframework.stereotype.Service;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.repository.StationRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationService {
    private final StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public List<Station> findAllById(List<Long> lineStationIds) {
        List<Station> allStations = stationRepository.findAll();

        return allStations.stream()
                .filter(station -> lineStationIds.stream()
                        .anyMatch(station::is))
                .collect(Collectors.toList());
    }


    public Station findByName(String name) {
        return stationRepository.findByName(name)
                .orElseThrow(() ->
                        new IllegalArgumentException("해당 이름의 역을 찾을 수 없습니다."));
    }
}
