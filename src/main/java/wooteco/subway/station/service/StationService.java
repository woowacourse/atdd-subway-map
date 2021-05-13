package wooteco.subway.station.service;

import org.springframework.stereotype.Service;
import wooteco.subway.controller.web.station.StationResponse;
import wooteco.subway.station.dao.StationDao;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.domain.StationName;
import wooteco.subway.station.exception.InvalidStationNameException;
import wooteco.subway.station.exception.WrongStationIdException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponse save(String stationName) {
        if (isDuplicatedName(new StationName(stationName))) {
            throw new InvalidStationNameException(String.format("역 이름이 중복되었습니다. 중복된 역 이름 : %s", stationName));
        }
        Station station = stationDao.save(new Station(stationName));
        return StationResponse.of(station);
    }

    private boolean isDuplicatedName(StationName stationName) {
        return stationDao.checkExistName(stationName);
    }

    public List<StationResponse> findAll() {
        List<Station> stations = stationDao.findAll();
        List<StationResponse> stationResponses = stations.stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
        return stationResponses;
    }

    public Station findById(Long id) {
        return stationDao.findById(id);
    }

    public void delete(Long id) {
        ifAbsent(id);
        stationDao.delete(id);
    }

    private void ifAbsent(Long id) {
        if (!stationDao.checkExistId(id)) {
            throw new WrongStationIdException("역이 존재하지 않습니다.");
        }
    }

    public void deleteAll() {
        stationDao.deleteAll();
    }
}
