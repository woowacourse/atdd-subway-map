package wooteco.subway.station.service;

import org.springframework.stereotype.Service;
import wooteco.subway.common.exception.not_found.NotFoundStationInfoException;
import wooteco.subway.common.exception.bad_request.WrongStationInfoException;
import wooteco.subway.station.dao.StationDao;
import wooteco.subway.station.domain.Station;

import java.util.List;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Station save(Station station) {
        if (isDuplicatedName(station)) {
            throw new WrongStationInfoException(String.format("역 이름이 중복되었습니다. 중복된 역 이름 : %s", station.getName()));
        }
        return stationDao.save(station);
    }


    private boolean isDuplicatedName(Station station) {
        return stationDao.checkExistName(station.getName());
    }

    public List<Station> findAll() {
        return stationDao.findAll();
    }

    public Station findById(Long id) {
        return stationDao.findById(id);
    }

    public void delete(Station station) {
        ifAbsent(station);
        stationDao.delete(station);
    }

    private void ifAbsent(Station station) {
        if (!stationDao.checkExistId(station.getId())) {
            throw new NotFoundStationInfoException("역이 존재하지 않습니다.");
        }
    }

    public void deleteAll() {
        stationDao.deleteAll();
    }
}
