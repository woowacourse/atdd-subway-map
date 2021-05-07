package wooteco.subway.station;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class StationRepository {
    private final StationDao stationDao;

    public StationRepository(final JdbcTemplate jdbcTemplate) {
        this.stationDao = new StationDao(jdbcTemplate);
    }

    public Station save(final Station station) {
        if (stationDao.isDuplicatedName(station.getName())) {
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

    public void delete(final Station station) {
        stationDao.delete(station.getId());
    }
}
