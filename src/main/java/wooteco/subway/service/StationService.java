package wooteco.subway.service;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    @Transactional
    public StationResponse create(String name) {
        try {
            Station newStation = stationDao.save(new Station(name));
            return new StationResponse(newStation.getId(), newStation.getName());
        } catch (DuplicateKeyException e) {
            throw new DuplicateKeyException("이미 존재하는 역 이름이 있습니다.");
        }
    }

    public List<StationResponse> findAll() {
        List<Station> stations = stationDao.findAll();
        return stations.stream()
                .map(it -> new StationResponse(it.getId(), it.getName()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void delete(Long id) {
        stationDao.deleteById(id);
    }
}
