package wooteco.subway.admin.service;

import org.springframework.stereotype.Service;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.StationCreateRequest;
import wooteco.subway.admin.dto.StationResponse;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;

import java.util.List;

@Service
public class StationService {
    private LineRepository lineRepository;
    private StationRepository stationRepository;

    public StationService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
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
        if (lineRepository.countByStationId(id) > 0) {
            throw new IllegalArgumentException("역이 노선에 존재합니다. 해당되는 노선에서 역을 모두 지우세요.");
        }
        stationRepository.deleteById(id);
    }
}
