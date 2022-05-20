package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.notfound.NotFoundStationException;
import wooteco.subway.exception.unknown.StationDeleteFailureException;
import wooteco.subway.exception.validation.StationNameDuplicateException;
import wooteco.subway.infra.repository.StationRepository;
import wooteco.subway.service.dto.StationServiceRequest;

@Service
@Transactional
public class SpringStationService implements StationService {

    private final StationRepository stationRepository;

    public SpringStationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    @Override
    public Station save(StationServiceRequest stationServiceRequest) {
        final String stationName = stationServiceRequest.getName();
        validateDuplicateName(stationName);

        return stationRepository.save(new Station(stationName));
    }

    private void validateDuplicateName(String name) {
        if (stationRepository.existByName(name)) {
            throw new StationNameDuplicateException(name);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<Station> findAll() {
        return stationRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public Station findById(Long stationId) {
        return stationRepository.findById(stationId)
                .orElseThrow(NotFoundStationException::new);
    }

    @Override
    public void deleteById(Long id) {
        validateExist(id);

        final long affectedRow = stationRepository.deleteById(id);

        if (affectedRow == 0) {
            throw new StationDeleteFailureException(id);
        }
    }

    private void validateExist(Long id) {
        if (stationRepository.existById(id)) {
            return;
        }

        throw new NotFoundStationException();
    }
}
