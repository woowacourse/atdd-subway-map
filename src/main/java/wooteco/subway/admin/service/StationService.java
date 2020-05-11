package wooteco.subway.admin.service;

import org.springframework.stereotype.Service;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.request.StationCreateRequest;
import wooteco.subway.admin.dto.response.StationResponse;
import wooteco.subway.admin.repository.StationRepository;

import java.util.List;

@Service
public class StationService {
    private final StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public List<Station> findAll() {
        return stationRepository.findAll();
    }

    public StationResponse createStation(StationCreateRequest request) {
        Station station = request.toStation();
        Station persistStation = stationRepository.save(station);

        return StationResponse.of(persistStation);
    }

    public void deleteById(Long id) {
        stationRepository.deleteById(id);
    }
}
