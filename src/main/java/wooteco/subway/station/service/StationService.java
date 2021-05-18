package wooteco.subway.station.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.station.controller.dto.StationRequest;
import wooteco.subway.station.controller.dto.StationResponse;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.domain.Stations;
import wooteco.subway.station.repository.StationRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationService {

    private final StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    @Transactional
    public StationResponse save(final StationRequest stationRequest) {
        Station station = stationRepository.save(stationRequest.toEntity());
        return StationResponse.from(station);
    }

    @Transactional(readOnly = true)
    public List<StationResponse> findAll() {
        return stationRepository.findAll().stream()
                .map(StationResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void delete(final Long id) {
        stationRepository.delete(id);
    }

    @Transactional(readOnly = true)
    public Stations findSortStationsByIds(List<Long> ids) {
        return new Stations(stationRepository.findByIds(ids)).sortStationsByIds(ids);
    }
}
