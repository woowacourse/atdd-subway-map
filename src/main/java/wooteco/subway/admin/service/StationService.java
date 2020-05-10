package wooteco.subway.admin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.StationResponse;
import wooteco.subway.admin.repository.StationRepository;

import java.util.List;

@Service
public class StationService {

    private final StationRepository stationRepository;

    @Autowired
    public StationService(final StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public List<StationResponse> findAllStations() {
        return StationResponse.listOf(stationRepository.findAll());
    }

    public Station create(final Station station) {
        return stationRepository.save(station);
    }

    public void deleteStationById(final Long id) {
        stationRepository.deleteById(id);
    }
}
