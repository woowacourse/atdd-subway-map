package wooteco.subway.service;

import java.util.List;

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
        Station station = stationRepository.saveStation(new Station(name));
        return DtoAssembler.stationResponse(station);
    }

    public List<StationResponse> findAll() {
        List<Station> stations = stationRepository.findStations();
        return DtoAssembler.stationResponses(stations);
    }

    @Transactional
    public void remove(Long id) {
        stationRepository.removeStation(id);
    }
}
