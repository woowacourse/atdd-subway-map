package wooteco.subway.admin.service;

import java.util.List;

import org.springframework.stereotype.Service;

import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.req.StationCreateRequest;
import wooteco.subway.admin.dto.res.StationResponse;
import wooteco.subway.admin.repository.StationRepository;

@Service
public class StationService {
    private final StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public StationResponse save(StationCreateRequest request) {
        Station saveStation = stationRepository.save(request.toStation());
        return StationResponse.of(saveStation);
    }

    public List<StationResponse> findAll() {
        return StationResponse.of(stationRepository.findAll());
    }

    public void deleteById(Long id) {
        stationRepository.deleteById(id);
    }
}
