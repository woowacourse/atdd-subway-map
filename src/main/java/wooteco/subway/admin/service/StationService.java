package wooteco.subway.admin.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.StationResponse;
import wooteco.subway.admin.repository.StationRepository;

import java.util.List;

@Service
@Transactional
public class StationService {
    private final StationRepository stationRepository;

    public StationService(final StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public List<StationResponse> findAllStations() {
        return StationResponse.listOf(stationRepository.findAll());
    }

    public StationResponse create(final Station station) {
        return StationResponse.of(stationRepository.save(station));
    }

    public void deleteStationById(final Long id) {
        stationRepository.deleteById(id);
    }
}
