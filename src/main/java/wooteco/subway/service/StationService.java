package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.repository.StationRepository;
import wooteco.subway.domain.Station;
import wooteco.subway.service.dto.StationRequest;
import wooteco.subway.service.dto.StationResponse;
import wooteco.subway.utils.exception.NameDuplicatedException;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class StationService {

    private final StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public StationResponse save(final StationRequest stationRequest) {
        validateDuplicateName(stationRequest);
        Station saveStation = stationRepository.save(new Station(stationRequest.getName()));

        return new StationResponse(saveStation);
    }

    private void validateDuplicateName(StationRequest stationRequest) {
        if (stationRepository.existByName(stationRequest.getName())) {
            throw new NameDuplicatedException("[ERROR] 이미 존재하는 역의 이름입니다.");
        }
    }

    @Transactional(readOnly = true)
    public List<StationResponse> showStations() {
        return stationRepository.findAll().stream()
                .map(StationResponse::new)
                .collect(Collectors.toList());
    }

    public void deleteStation(final Long id) {
        stationRepository.deleteById(id);
    }
}
