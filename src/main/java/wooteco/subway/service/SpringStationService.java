package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.StationRepository;
import wooteco.subway.dao.entity.StationEntity;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.StationDeleteFailureException;
import wooteco.subway.service.dto.StationServiceRequest;

@Service
public class SpringStationService implements StationService {

    private final StationRepository stationRepository;

    public SpringStationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    @Transactional
    @Override
    public Station save(StationServiceRequest stationServiceRequest) {
        StationEntity stationEntity = new StationEntity(stationServiceRequest.getName());
        final StationEntity saved = stationRepository.save(stationEntity);
        return new Station(saved.getId(), saved.getName());
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
        final long affectedRow = stationRepository.deleteById(id);

        if (affectedRow == 0) {
            throw new StationDeleteFailureException(id);
        }
    }
}
