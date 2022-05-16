package wooteco.subway.service;

import static java.util.stream.Collectors.toList;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.error.exception.NotFoundException;

@Transactional(readOnly = true)
@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    @Transactional
    public StationResponse save(StationRequest stationRequest) {
        if (stationDao.existsByName(stationRequest.getName())) {
            throw new IllegalArgumentException(stationRequest.getName() + "은 이미 존재하는 지하철역 이름입니다.");
        }

        Station station = stationDao.save(new Station(stationRequest.getName()));
        return new StationResponse(station);
    }

    public StationResponse findById(Long id) {
        return new StationResponse(getStation(id));
    }

    private Station getStation(Long id) {
        return stationDao.findById(id)
                .orElseThrow(() -> new NotFoundException(id + "의 지하철역은 존재하지 않습니다."));
    }

    public List<StationResponse> findAll() {
        return stationDao.findAll()
                .stream()
                .map(StationResponse::new)
                .collect(toList());
    }

    @Transactional
    public void deleteById(Long id) {
        stationDao.deleteById(id);
    }
}
