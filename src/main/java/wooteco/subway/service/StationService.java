package wooteco.subway.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationResponse;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(final StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Station save(final Station station) {
        if (stationDao.existByName(station.getName())) {
            throw new IllegalStateException("이미 존재하는 역 이름입니다.");
        }
        return stationDao.save(station);
    }

    public List<Station> findAll() {
        return stationDao.findAll();
    }

    public void delete(final Long stationId) {
        if (!stationDao.existById(stationId)) {
            throw new NoSuchElementException("없는 Station 입니다.");
        }
        stationDao.delete(stationId);
    }

    public List<StationResponse> findByStationsId(List<Long> stationsId) {
        return stationsId.stream()
                .map(id -> StationResponse.from(stationDao.findById(id)))
                .collect(Collectors.toList());
    }
}
