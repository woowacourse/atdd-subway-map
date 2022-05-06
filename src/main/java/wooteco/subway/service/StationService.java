package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StationService {
    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Station save(Station station) {
        stationDao.checkDuplication(station);
        return stationDao.save(station);
    }

    public List<Station> findAll(){
        return stationDao.findAll();
    }

    public void delete(Long id) {
        stationDao.delete(id);
    }

    public Station findById(Long id){
        return stationDao.findById(id);
    }

    public List<Station> findByIdIn(Set<Long> ids){
        List<String> stringIds = ids.stream()
                .map(id -> Long.toString(id))
                .collect(Collectors.toList());

        return stationDao.findByIdIn(stringIds);
    }
}
