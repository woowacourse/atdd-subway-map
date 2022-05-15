package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.reopository.StationRepository;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.exception.BadRequestException;

@Service
public class StationService {

    private final StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public StationResponse create(StationRequest stationRequest) {
        Station station = new Station(stationRequest.getName());
        validateDuplicateName(station);
        Long stationId = stationRepository.save(station);
        return new StationResponse(stationId, stationRequest.getName());
    }

    public List<StationResponse> showAll() {
        List<Station> stations = stationRepository.findAll();
        return stations.stream()
                .map(value -> new StationResponse(value.getId(), value.getName()))
                .collect(Collectors.toList());
    }

    public void removeById(Long id) {
        stationRepository.deleteById(id);
    }

    private void validateDuplicateName(Station station) {
        if (stationRepository.existByName(station.getName())) {
            throw new BadRequestException("지하철 역 이름은 중복될 수 없습니다.");
        }
    }
}
