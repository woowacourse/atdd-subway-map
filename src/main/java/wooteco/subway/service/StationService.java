package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.exception.StationNotFoundException;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(final StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Station create(final StationRequest stationRequest) {
        final Station station = stationRequest.toEntity();
        return stationDao.save(station);
    }

    public List<Station> show() {
        return stationDao.findAll();
    }

    public void delete(final Long id) {
        final Station targetLine = stationDao.findById(id)
            .orElseThrow(() -> new StationNotFoundException("이미 존재하는 지하철역 이름입니다."));
        stationDao.delete(targetLine);
    }
}
