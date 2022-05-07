package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

@Service
@Transactional
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponse save(StationRequest stationRequest) {
        validateByName(stationRequest.getName());
        Station station = new Station(stationRequest.getName());
        return toStationResponse(stationDao.save(station));
    }

    private void validateByName(String name) {
        if (stationDao.existByName(name)) {
            throw new IllegalArgumentException("이미 존재하는 지하철역 입니다.");
        }
    }

    private StationResponse toStationResponse(Station station) {
        return new StationResponse(station.getId(), station.getName());
    }

    @Transactional(readOnly = true)
    public List<StationResponse> findAll() {
        return stationDao.findAll().stream()
            .map(this::toStationResponse)
            .collect(Collectors.toList());
    }


    public void delete(Long id) {
        validateById(id);
        stationDao.deleteById(id);
    }

    private void validateById(Long id) {
        if (!stationDao.existById(id)) {
            throw new IllegalArgumentException("존재하지 않는 id입니다.");
        }
    }
}
