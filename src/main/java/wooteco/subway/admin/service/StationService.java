package wooteco.subway.admin.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.StationCreateRequest;
import wooteco.subway.admin.dto.StationResponse;
import wooteco.subway.admin.exception.DuplicateStationException;
import wooteco.subway.admin.repository.StationRepository;

@Transactional
@Service
public class StationService {
    private final StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public StationResponse findById(Long id) {
        Station station = stationRepository.findById(id)
                .orElseThrow(IllegalArgumentException::new);
        return StationResponse.of(station);
    }

    public StationResponse save(StationCreateRequest request) {
        Station station = new Station(request.getName());
        stationRepository.findByName(station.getName())
                .ifPresent(this::throwDuplicateException);
        Station savedStation = stationRepository.save(station);
        return StationResponse.of(savedStation);
    }

    private void throwDuplicateException(Station station) {
        throw new DuplicateStationException(station.getName());
    }
}
