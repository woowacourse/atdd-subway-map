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
        validateName(station.getName());

        final Long id = stationDao.save(station.getName());
        return findById(id);
    }

    private void validateName(final String name) {
        if (stationDao.isExistingName(name)) {
            throw new StationException("이미 존재하는 역 이름입니다.");
        }
    }

    public List<Station> findAll() {
        return stationDao.findAll();
    }

    public void delete(final Long id) {
        validateExisting(id);

        stationDao.delete(id);
    }

    private void validateExisting(final Long id) {
        findById(id);
    }

    public Station findById(final Long id) {
        return stationDao.findById(id)
                .orElseThrow(() -> new StationException("존재하지 않는 역입니다."));
    }
}
