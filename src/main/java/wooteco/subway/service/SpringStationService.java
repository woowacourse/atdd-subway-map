package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.notfound.NotFoundStationException;
import wooteco.subway.exception.unknown.StationDeleteFailureException;
import wooteco.subway.exception.validation.StationNameDuplicateException;
import wooteco.subway.infra.dao.StationDao;
import wooteco.subway.infra.entity.StationEntity;
import wooteco.subway.service.dto.StationServiceRequest;

@Service
public class SpringStationService implements StationService {

    private final StationDao stationRepository;

    public SpringStationService(StationDao stationRepository) {
        this.stationRepository = stationRepository;
    }

    @Transactional
    @Override
    public Station save(StationServiceRequest stationServiceRequest) {
        validateDuplicateName(stationServiceRequest);

        StationEntity stationEntity = new StationEntity(stationServiceRequest.getName());
        final StationEntity saved = stationRepository.save(stationEntity);

        return new Station(saved.getId(), saved.getName());
    }

    private void validateDuplicateName(StationServiceRequest stationServiceRequest) {
        if (stationRepository.existsByName(stationServiceRequest.getName())) {
            throw new StationNameDuplicateException(stationServiceRequest.getName());
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<Station> findAll() {
        final List<StationEntity> stationEntities = stationRepository.findAll();

        return stationEntities.stream()
                .map(entity -> new Station(entity.getId(), entity.getName()))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        validateExist(id);

        final long affectedRow = stationRepository.deleteById(id);

        if (affectedRow == 0) {
            throw new StationDeleteFailureException(id);
        }
    }

    private void validateExist(Long id) {
        if (stationRepository.existsById(id)) {
            return;
        }

        throw new NotFoundStationException();
    }
}
