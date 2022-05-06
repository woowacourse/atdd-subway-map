package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.exception.DataLengthException;
import wooteco.subway.exception.DuplicateNameException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponse create(String name) {
        validateDataSize(name);
        Station station = new Station(name);
        validateDuplicationName(station);
        Station newStation = stationDao.save(station);
        return new StationResponse(newStation.getId(), newStation.getName());
    }

    private void validateDataSize(String name) {
        if (name.isEmpty() || name.length() > 255) {
            throw new DataLengthException("역 이름이 빈 값이거나 최대 범위를 초과했습니다.");
        }
    }

    private void validateDuplicationName(Station station) {
        List<Station> stations = stationDao.findAll();
        if (stations.contains(station)) {
            throw new DuplicateNameException("중복된 역 이름이 있습니다.");
        }
    }

    public List<StationResponse> findAll() {
        List<Station> stations = stationDao.findAll();
        return stations.stream()
                .map(it -> new StationResponse(it.getId(), it.getName()))
                .collect(Collectors.toList());
    }

    public void delete(Long id) {
        stationDao.deleteById(id);
    }
}
