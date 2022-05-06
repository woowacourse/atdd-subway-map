package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.StationRepository;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.utils.exception.ExceptionMessages;
import wooteco.subway.utils.exception.NameDuplicatedException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StationService {

    private final StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    @Transactional
    public StationResponse save(final StationRequest stationRequest) {
        validateDuplicateName(stationRepository.findByName(stationRequest.getName()));

        Station saveStation = stationRepository.save(new Station(stationRequest.getName()));
        return new StationResponse(saveStation.getId(), saveStation.getName());
    }

    private void validateDuplicateName(final Optional<Station> station) {
        station.ifPresent(s -> {
            throw new NameDuplicatedException(ExceptionMessages.NAME_DUPLICATE_MESSAGE);
        });
    }

    public List<StationResponse> showStations() {
        return stationRepository.findAll().stream()
                .map(station -> new StationResponse(station.getId(), station.getName()))
                .collect(Collectors.toList());
    }

    public void deleteStation(final Long id) {
        stationRepository.deleteById(id);
    }
}
