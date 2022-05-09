package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.StationDao;
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
        Station station = new Station(stationSaveRequest.getName());
        validateDuplicationName(station);
        stationDao.save(station);
        return new StationSaveResponse(station.getId(), station.getName());
    }

    private void validateDuplicationName(Station station) {
        if (stationDao.exists(station)) {
            throw new IllegalArgumentException("중복된 이름이 존재합니다.");
        }
    }

    public List<StationFindResponse> findAll() {
        List<Station> stations = stationDao.findAll();
        return stations.stream()
            .map(i -> new StationFindResponse(i.getId(), i.getName()))
            .collect(Collectors.toList());
    }

    public boolean deleteById(Long id) {
        return stationDao.deleteById(id);
    }
}
