package wooteco.subway.station.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.exception.DuplicatedNameException;
import wooteco.subway.exception.NotFoundException;
import wooteco.subway.station.Station;
import wooteco.subway.station.dto.StationRequest;
import wooteco.subway.station.dto.StationResponse;
import wooteco.subway.station.repository.StationDao;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationService {
    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    @Transactional
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

    public Station findBy(Long id) {
        return stationDao.findBy(id);
    }

    public void validateExistStations(Long upId, Long downId) {
        if (!isExistingStation(upId)) {
            throw new NotFoundException("등록되지 않은 역은 상행 혹은 하행역으로 추가할 수 없습니다.");
        }
        if (!isExistingStation(downId)) {
            throw new NotFoundException("등록되지 않은 역은 상행 혹은 하행역으로 추가할 수 없습니다.");
        }
    }

    @Transactional(readOnly = true)
    public List<StationResponse> findAllStations() {
        List<Station> stations = stationDao.findAll();
        return stations.stream()
                .map(station -> new StationResponse(station.getId(), station.getName()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void delete(Long id) {
        stationDao.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<StationResponse> findStationsByIds(List<Long> stationIds) {
        return stationIds.stream()
                .map(id -> stationDao.findBy(id))
                .map(StationResponse::new)
                .collect(Collectors.toList());
    }

    public boolean isExistingStation(Long stationId) {
        return stationDao.isExistingStation(stationId);
    }
}
