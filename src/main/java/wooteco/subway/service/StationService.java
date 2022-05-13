package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.repository.StationRepository;
import wooteco.subway.utils.exception.NameDuplicatedException;

@Service
public class StationService {

    private final StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    @Transactional
    public StationResponse save(final StationRequest stationRequest) {
        validateDuplicateName(stationRepository.findByName(stationRequest.getName()).isPresent());

        Station station = stationRepository.save(new Station(stationRequest.getName()));
        return new StationResponse(station.getId(), station.getName());
    }

    private void validateDuplicateName(final boolean isDuplicateName) {
        if (isDuplicateName) {
            throw new NameDuplicatedException(NameDuplicatedException.NAME_DUPLICATE_MESSAGE);
        }
    }

    public List<StationResponse> showStations() {
        return stationRepository.findAll().stream()
                .map(StationResponse::new)
                .collect(Collectors.toList());
    }

    public void deleteStation(final Long id) {
        stationRepository.deleteById(id);
    }
}
