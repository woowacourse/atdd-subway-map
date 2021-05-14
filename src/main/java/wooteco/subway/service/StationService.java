package wooteco.subway.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.station.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.station.DuplicatedStationException;
import wooteco.subway.exception.station.StationNotFoundException;

import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class StationService {

    private final StationDao stationDao;

    public Station save(Station station) {
        if (stationDao.findByName(station.getName()).isPresent()) {
            throw new DuplicatedStationException();
        }
        return stationDao.save(station);
    }

    public List<Station> findAll() {
        return stationDao.findAll();
    }

    public void delete(Long id) {
        stationDao.delete(id);
    }

    public Station find(Long id) {
        return stationDao.findById(id).orElseThrow(StationNotFoundException::new);
    }
}
