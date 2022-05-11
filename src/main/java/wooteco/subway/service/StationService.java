package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.service.dto.station.StationFindResponse;
import wooteco.subway.service.dto.station.StationSaveRequest;
import wooteco.subway.service.dto.station.StationSaveResponse;

@Service
@Transactional(readOnly = true)
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    @Transactional
    public StationSaveResponse save(StationSaveRequest stationSaveRequest) {
        validateDuplicationName(stationSaveRequest.getName());
        Station savedStation = stationDao.save(new Station(stationSaveRequest.getName()));
        return new StationSaveResponse(savedStation.getId(), savedStation.getName());
    }

    private void validateDuplicationName(String name) {
        if (stationDao.existsByName(name)) {
            throw new IllegalArgumentException("중복된 이름이 존재합니다.");
        }
    }

    public List<StationFindResponse> findAll() {
        List<Station> stationEntities = stationDao.findAll();
        return stationEntities.stream()
            .map(i -> new StationFindResponse(i.getId(), i.getName()))
            .collect(Collectors.toList());
    }

    public boolean deleteById(Long id) {
        return stationDao.deleteById(id);
    }
}
