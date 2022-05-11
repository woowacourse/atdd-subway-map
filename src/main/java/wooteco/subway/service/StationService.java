package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.exception.BadRequestException;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponse create(StationRequest stationRequest) {
        Station station = new Station(stationRequest.getName());
        validateDuplicateName(station);
        Station savedStation = stationDao.save(station);
        return new StationResponse(savedStation.getId(), savedStation.getName());
    }

    public List<StationResponse> showAll() {
        List<Station> stations = stationDao.findAll();
        return stations.stream()
                .map(value -> new StationResponse(value.getId(), value.getName()))
                .collect(Collectors.toList());
    }

    public void removeById(Long id) {
        stationDao.deleteById(id);
    }

    private void validateDuplicateName(Station station) {
        if (stationDao.existByName(station.getName())) {
            throw new BadRequestException("지하철 역 이름은 중복될 수 없습니다.");
        }
    }
}
