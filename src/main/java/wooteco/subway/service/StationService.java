package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.entity.StationEntity;
import wooteco.subway.repository.StationDao;

@Service
@Transactional
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponse createStation(StationRequest stationRequest) {
        validateNotToDuplicateName(stationRequest.getName());

        StationEntity newStationEntity = stationDao
            .save(stationRequest.toDomain());
        return new StationResponse(newStationEntity.getId(), newStationEntity.getName());
    }

    private void validateNotToDuplicateName(String name) {
        if (stationDao.hasStationWithName(name)) {
            throw new IllegalArgumentException("이미 존재하는 이름입니다.");
        }
    }

    @Transactional(readOnly = true)
    public List<StationResponse> showStations() {
        List<StationEntity> stationEntities = stationDao.findAll();
        return stationEntities.stream()
            .map(stationEntity -> new StationResponse(stationEntity.getId(),
                stationEntity.getName()))
            .collect(Collectors.toList());
    }

    public void delete(Long id) {
        validateToExistId(id);
        stationDao.delete(id);
    }

    private void validateToExistId(Long id) {
        if (!stationDao.hasStationWithId(id)) {
            throw new IllegalArgumentException("존재하지 않는 ID입니다.");
        }
    }
}
