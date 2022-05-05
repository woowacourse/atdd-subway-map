package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.JdbcStationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.exception.BadRequestException;

@Service
@Transactional(readOnly = true)
public class StationService {

    private final JdbcStationDao jdbcStationDao;

    public StationService(JdbcStationDao jdbcStationDao) {
        this.jdbcStationDao = jdbcStationDao;
    }

    @Transactional
    public StationResponse create(StationRequest stationRequest) {
        Station station = new Station(stationRequest.getName());
        validateDuplicateName(station);
        Station savedStation = jdbcStationDao.save(station);
        return new StationResponse(savedStation.getId(), savedStation.getName());
    }

    public List<StationResponse> showAll() {
        List<Station> stations = jdbcStationDao.findAll();
        return stations.stream()
            .map(it -> new StationResponse(it.getId(), it.getName()))
            .collect(Collectors.toList());
    }

    @Transactional
    public void removeById(Long id) {
        jdbcStationDao.deleteById(id);
    }

    private void validateDuplicateName(Station station) {
        if (jdbcStationDao.existByName(station.getName())) {
            throw new BadRequestException("지하철 역 이름은 중복될 수 없습니다.");
        }
    }
}
