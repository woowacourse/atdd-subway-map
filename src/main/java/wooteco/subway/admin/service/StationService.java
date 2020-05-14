package wooteco.subway.admin.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.request.StationCreateRequest;
import wooteco.subway.admin.dto.resopnse.StationResponse;
import wooteco.subway.admin.exception.DuplicateNameException;
import wooteco.subway.admin.exception.NotFoundException;
import wooteco.subway.admin.repository.StationRepository;

@Service
public class StationService {

    private final StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public List<StationResponse> getAll() {
        List<Station> stations = stationRepository.findAll();
        return stations.stream()
            .map(StationResponse::of)
            .collect(Collectors.toList());
    }

    public StationResponse getById(Long id) {
        Station station = findById(id);
        return StationResponse.of(station);
    }

    public Long save(StationCreateRequest stationCreateRequest) {
        validateDuplication(stationCreateRequest.getName());
        Station station = stationCreateRequest.toStation();
        return stationRepository.save(station).getId();
    }

    private Station findById(Long id) {
        return stationRepository.findById(id)
            .orElseThrow(() -> new NotFoundException(id));
    }

    public void deleteById(Long id) {
        stationRepository.deleteById(id);
    }

    private void validateDuplication(String name) {
        boolean exist = stationRepository.existsStationBy(name);
        if (exist) {
            throw new DuplicateNameException(name);
        }
    }
}
