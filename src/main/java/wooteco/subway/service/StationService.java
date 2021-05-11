package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.exception.DuplicationException;
import wooteco.subway.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class StationService {
    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    @Transactional(readOnly = false)
    public StationResponse save(String stationName) {
        Station station = Station.from(stationName);
        if (stationDao.findByName(stationName).isPresent()) {
            throw new DuplicationException("같은 이름의 역이 있습니다;");
        }
        return StationResponse.of(stationDao.save(station));
    }

    public List<StationResponse> findAll() {
        List<Station> stations = stationDao.findAll();
        return stations.stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = false)
    public void delete(Long id) {
        stationDao.findById(id)
                .orElseThrow(() -> new NotFoundException("삭제하려는 역이 존재하지 않습니다"));
        stationDao.delete(id);
    }
}
