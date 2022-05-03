package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.StationRepository;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.utils.exception.NameDuplicatedException;

import java.util.Objects;

@Service
public class StationService {

    private final StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public Station save(StationRequest stationRequest) {
        Station findStation = stationRepository.findByName(stationRequest.getName());
        validateDuplicateName(findStation);

        return stationRepository.save(new Station(stationRequest.getName()));
    }

    private void validateDuplicateName(Station station) {
        if (Objects.nonNull(station)) {
            throw new NameDuplicatedException("[ERROR] 이미 존재하는 이름입니다.");
        }
    }

}
