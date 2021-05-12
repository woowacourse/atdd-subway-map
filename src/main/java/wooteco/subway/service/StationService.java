package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import wooteco.subway.domain.station.Station;
import wooteco.subway.domain.station.StationDao;
import wooteco.subway.web.exception.NotFoundException;
import wooteco.subway.web.exception.SubwayHttpException;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Station add(Station station) {
        Long id = addStation(station);
        return findById(id);
    }

    private Long addStation(Station station) {
        return stationDao.save(station)
                .orElseThrow(() -> new SubwayHttpException("중복된 역 이름입니다"));
    }

    public List<Station> findAll() {
        return stationDao.findAll();
    }

    private Station findById(Long id) {
        return stationDao.findById(id)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 역입니다"));
    }

    public void delete(Long id) {
        findById(id);
        stationDao.delete(id);
    }
}
