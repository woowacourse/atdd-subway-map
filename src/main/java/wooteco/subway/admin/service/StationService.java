package wooteco.subway.admin.service;

import org.springframework.stereotype.Service;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.StationResponse;
import wooteco.subway.admin.repository.StationRepository;

import java.util.List;

@Service
public class StationService {
    private StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public Station save(Station station) {
        return stationRepository.save(station);
    }

    public List<StationResponse> showStations() {
        return StationResponse.listOf(stationRepository.findAll());
    }

    public void deleteStationByName(String name) {
        stationRepository.deleteByName(name);
    }
}
