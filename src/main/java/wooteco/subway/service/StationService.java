package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

@Service
@Transactional
public class StationService {

    private final StationDao stationDao;

    public StationService(final StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponse create(final StationRequest request) {
        final Station station = new Station(request.getName());
        final Station savedStation = stationDao.insert(station)
                .orElseThrow(() -> new IllegalArgumentException("중복된 이름의 역은 저장할 수 없습니다."));
        return new StationResponse(savedStation.getId(), savedStation.getName());
    }

    @Transactional(readOnly = true)
    public List<StationResponse> findAll() {
        return stationDao.findAll()
                .stream()
                .map(it -> new StationResponse(it.getId(), it.getName()))
                .collect(Collectors.toList());
    }

    public void delete(final Long id) {
        stationDao.deleteById(id);
    }
}
