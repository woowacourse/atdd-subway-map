package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.service.dto.StationServiceRequest;
import wooteco.subway.service.dto.StationServiceResponse;

@Service
@Transactional(readOnly = true)
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    @Transactional
    public StationServiceResponse save(StationServiceRequest stationServiceRequest) {
        validateDuplicationName(stationServiceRequest.getName());
        Station savedStation = stationDao.save(new Station(stationServiceRequest.getName()));
        return new StationServiceResponse(savedStation.getId(), savedStation.getName());
    }

    private void validateDuplicationName(String name) {
        if (stationDao.existsByName(name)) {
            throw new IllegalArgumentException("중복된 이름이 존재합니다.");
        }
    }

    public List<StationServiceResponse> findAll() {
        List<Station> stationEntities = stationDao.findAll();
        return stationEntities.stream()
            .map(i -> new StationServiceResponse(i.getId(), i.getName()))
            .collect(Collectors.toList());
    }

    public boolean deleteById(Long id) {
        return stationDao.deleteById(id);
    }
}
