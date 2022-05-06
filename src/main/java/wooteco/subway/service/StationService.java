package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public long save(final Station station) {
        validateName(station);
        return stationDao.save(station);
    }

    public List<Station> findAll() {
        return stationDao.findAll();
    }

    public void delete(final Long id) {
        validateExistedLine(id);
        stationDao.delete(id);
    }

    private void validateName(final Station station) {
        if (stationDao.existStationByName(station.getName())) {
            throw new IllegalArgumentException("지하철역 이름이 중복됩니다.");
        }
    }

    private void validateExistedLine(final Long id) {
        if (!stationDao.existStationById(id)) {
            throw new IllegalArgumentException("존재하지 않는 지하철역입니다.");
        }
    }
}
