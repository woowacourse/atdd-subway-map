package wooteco.subway.admin.service;

import org.springframework.stereotype.Service;

import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.StationCreateRequest;
import wooteco.subway.admin.exception.ExistingNameException;
import wooteco.subway.admin.repository.StationRepository;

@Service
public class StationService {

    private final StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public Long save(StationCreateRequest stationCreateRequest) {
        validateDuplication(stationCreateRequest.getName());
        Station station = stationCreateRequest.toStation();
        return stationRepository.save(station).getId();
    }



    private void validateDuplication(String name) {
        boolean exist = stationRepository.existsStationBy(name);
        if (exist) {
            throw new ExistingNameException(name);
        }
    }
}
