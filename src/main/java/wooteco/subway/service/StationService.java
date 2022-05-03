package wooteco.subway.service;

import java.util.List;
import wooteco.subway.dao.InmemoryStationDao;
import wooteco.subway.domain.Station;

public class StationService {

    private final InmemoryStationDao inmemoryStationDao;

    public StationService(final InmemoryStationDao inmemoryStationDao) {
        this.inmemoryStationDao = inmemoryStationDao;
    }

    public Station save(final Station station) {
        if (inmemoryStationDao.existByName(station.getName())) {
            throw new IllegalStateException("이미 존재하는 역 이름입니다.");
        }
        return inmemoryStationDao.save(station);
    }

    public List<Station> findAll() {
        return inmemoryStationDao.findAll();
    }

    public void delete(final Long stationId) {
        inmemoryStationDao.delete(stationId);
    }
}
