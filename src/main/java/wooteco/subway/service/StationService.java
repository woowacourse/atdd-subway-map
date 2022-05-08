package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.DataNotFoundException;
import wooteco.subway.exception.DuplicateNameException;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(final StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Station createStation(final Station station) {
        validateDuplicateName(station);
        return stationDao.save(station);
    }

    public List<Station> getAllStations() {
        return stationDao.findAll();
    }

    public void delete(final Long id) {
        validateExist(id);
        stationDao.deleteById(id);
    }

    private void validateDuplicateName(final Station station) {
        if (stationDao.existByName(station.getName())) {
            throw new DuplicateNameException("이미 존재하는 지하철 역입니다.");
        }
    }

    private void validateExist(final Long id) {
        if (!stationDao.existById(id)) {
            throw new DataNotFoundException("삭제하려는 지하철 역 ID가 존재하지 않습니다.");
        }
    }
}
