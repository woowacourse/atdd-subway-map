package wooteco.subway.admin.service;

import org.springframework.stereotype.Service;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.StationCreateRequest;
import wooteco.subway.admin.dto.StationResponse;
import wooteco.subway.admin.repository.StationRepository;

import java.util.List;

@Service
public class StationService {
    private StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public StationResponse addStation(StationCreateRequest request) {
        Station station = request.toStation();
        validateDuplicate(station);
        Station persistStation = stationRepository.save(station);

        return StationResponse.of(persistStation);
    }

    private void validateDuplicate(Station station) {
        if (stationRepository.findByName(station.getName()).isPresent()) {
            throw new IllegalArgumentException("중복된 이름이 존재합니다.");
        }
    }

    public List<StationResponse> findAll() {
        return StationResponse.listOf(stationRepository.findAll());
    }

    public void deleteById(Long id) {
        stationRepository.deleteById(id);
    }
}
