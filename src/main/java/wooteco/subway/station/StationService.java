package wooteco.subway.station;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class StationService {
    private final StationDao stationDao;

    public StationService(final StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Station save(final Station station) {
        if (stationDao.isExistingName(station.getName())) {
            throw new StationException("이미 존재하는 역 이름입니다.");
        }

        final Long id = stationDao.save(station.getName());
        return findById(id);
    }

    public Station findById(final Long id) {
        return stationDao.findById(id);
    }

    public List<Station> findAll() {
        return stationDao.findAll();
    }

    public void delete(final Long id) {
        stationDao.delete(id);
    }
}
