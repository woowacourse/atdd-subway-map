package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.station.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.station.StationResponse;
import wooteco.subway.exception.NotFoundException;

@Service
@Transactional(readOnly = true)
public class StationService {

    private final StationDao stationDao;

    public StationService(final StationDao stationDao) {
        this.stationDao = stationDao;
    }

    @Transactional
    public StationResponse save(final Station station) {
        if (stationDao.existByName(station.getName())) {
            throw new IllegalStateException("이미 존재하는 역 이름입니다.");
        }
        long savedStationId = stationDao.save(station);
        return StationResponse.from(findById(savedStationId));
    }

    private Station findById(final long savedStationId) {
        return stationDao.findById(savedStationId);
    }

    public List<StationResponse> findAll() {
        return stationDao.findAll()
                .stream()
                .map(StationResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void delete(final Long stationId) {
        validateExistStation(stationId);
        stationDao.delete(stationId);
    }

    private void validateExistStation(final Long stationId) {
        if (!stationDao.existById(stationId)) {
            throw new NotFoundException("존재하지 않는 Station입니다.");
        }
    }
}
