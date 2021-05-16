package wooteco.subway.station.service;

import org.springframework.stereotype.Service;
import wooteco.subway.exception.DuplicatedNameException;
import wooteco.subway.exception.NotFoundException;
import wooteco.subway.station.Station;
import wooteco.subway.station.dto.StationRequest;
import wooteco.subway.station.dto.StationResponse;
import wooteco.subway.station.repository.StationDao;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationService {
    private static final int VALID_STATION_SIZE = 2;

    private StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponse save(StationRequest stationRequest) {
        validateStationName(stationRequest);
        Station station = stationRequest.toEntity();
        Station newStation = stationDao.save(station);
        return new StationResponse(newStation);
    }

    private void validateStationName(StationRequest stationRequest) {
        if (checkNameDuplicate(stationRequest)) {
            throw new DuplicatedNameException("중복된 이름의 역이 존재합니다.");
        }
    }

    private boolean checkNameDuplicate(StationRequest stationRequest) {
        return stationDao.findByName(stationRequest.getName());
    }

    public List<StationResponse> findAllStations() {
        List<Station> stations = stationDao.findAll();
        return stations.stream()
                .map(station -> new StationResponse(station.getId(), station.getName()))
                .collect(Collectors.toList());
    }

    public void delete(Long id) {
        stationDao.deleteById(id);
    }

    public void validateStations(Long upStationId, Long downStationId) {
        List<Station> stations = new ArrayList<>();
        stations.add(stationDao.findBy(upStationId));
        stations.add(stationDao.findBy(downStationId));

        if (stations.size() != VALID_STATION_SIZE) {
            throw new NotFoundException("등록되지 않은 역은 상행 혹은 하행역으로 추가할 수 없습니다.");
        }
    }

    public List<StationResponse> findStationsByIds(List<Long> stationIds) {
        return stationIds.stream()
                .map(id -> stationDao.findBy(id))
                .map(StationResponse::new)
                .collect(Collectors.toList());
    }

    public boolean isExistingStation(Station station) {
        return stationDao.isExistingStation(station);
    }
}
