package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.StationDao;
import wooteco.subway.dao.entity.StationEntity;
import wooteco.subway.domain.Station;
import wooteco.subway.service.dto.station.StationFindResponse;
import wooteco.subway.service.dto.station.StationSaveRequest;
import wooteco.subway.service.dto.station.StationSaveResponse;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationSaveResponse save(StationSaveRequest stationSaveRequest) {
        validateDuplicationName(stationSaveRequest.getName());
        StationEntity savedStationEntity = stationDao.save(new Station(stationSaveRequest.getName()));
        return new StationSaveResponse(savedStationEntity.getId(), savedStationEntity.getName());
    }

    private void validateDuplicationName(String name) {
        if (stationDao.existsByName(name)) {
            throw new IllegalArgumentException("중복된 이름이 존재합니다.");
        }
    }

    public List<StationFindResponse> findAll() {
        List<StationEntity> stationEntities = stationDao.findAll();
        return stationEntities.stream()
            .map(i -> new StationFindResponse(i.getId(), i.getName()))
            .collect(Collectors.toList());
    }

    public boolean deleteById(Long id) {
        return stationDao.deleteById(id);
    }
}
