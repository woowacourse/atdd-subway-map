package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import wooteco.subway.domain.station.Station;
import wooteco.subway.domain.station.StationRepository;
import wooteco.subway.service.dto.DtoAssembler;
import wooteco.subway.service.dto.station.StationResponse;

@Service
public class StationService {

    private final StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    @Transactional
    public StationResponse create(String name) {
        Long stationId = stationRepository.saveStation(new Station(name));
        Station station = stationRepository.findStationById(stationId);
        return DtoAssembler.stationResponse(station);
    }

    public List<StationResponse> findAll() {
        return stationRepository.findStations()
                .stream()
                .map(DtoAssembler::stationResponse)
                .collect(Collectors.toUnmodifiableList());
    }

    @Transactional
    public void remove(Long id) {
        stationRepository.removeStation(id);
    }
}
