package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSystemException;

import wooteco.subway.dao.StationJdbcDao;
import wooteco.subway.domain.Station;
import wooteco.subway.domain.Stations;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.exception.ClientException;

@Service
public class StationService {

    private final StationJdbcDao stationDao;

    public StationService(final StationJdbcDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponse save(StationRequest request) {
        Stations stations = stationDao.findAll();
        Station station = new Station(request.getName());
        stations.add(station);

        Station newStation = stationDao.save(station);
        return new StationResponse(newStation.getId(), newStation.getName());
    }

    public List<StationResponse> findAll() {
        Stations stations = stationDao.findAll();
        return stations.getStations()
                .stream()
                .map(it -> new StationResponse(it.getId(), it.getName()))
                .collect(Collectors.toList());
    }

    public int delete(long id) {
        try {
            return stationDao.delete(id);
        } catch (TransactionSystemException exception) {
            throw new ClientException("구간에 등록되어 있는 역은 제거할 수 없습니다.");
        }
    }
}
