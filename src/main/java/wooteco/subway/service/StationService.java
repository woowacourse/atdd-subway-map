package wooteco.subway.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.request.StationRequest;
import wooteco.subway.dto.response.StationResponse;
import wooteco.subway.entity.StationEntity;

@Service
public class StationService {

    private static final String DUPLICATE_NAME_ERROR = "이미 같은 이름의 지하철역이 존재합니다.";
    private static final String NOT_EXIST_ERROR = "해당 지하철역이 존재하지 않습니다.";

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponse createStation(StationRequest stationRequest) {
        StationEntity stationEntity = stationRequest.toEntity();
        Optional<StationEntity> wrappedStationEntity = stationDao.findByName(stationRequest.getName());
        if (wrappedStationEntity.isPresent()) {
            throw new DuplicateKeyException(DUPLICATE_NAME_ERROR);
        }
        StationEntity newStationEntity = stationDao.save(stationEntity);
        Station newStation = new Station(newStationEntity.getId(), newStationEntity.getName());
        return StationResponse.of(newStation);
    }

    public List<StationResponse> findAllStations() {
        List<StationEntity> stations = stationDao.findAll();
        return stations.stream()
                .map(stationEntity -> new Station(stationEntity.getId(), stationEntity.getName()))
                .map(StationResponse::of)
                .collect(Collectors.toList());
    }

    public Station findById(Long id) {
        Optional<StationEntity> stationEntity = stationDao.findById(id);
        if (stationEntity.isEmpty()) {
            throw new NoSuchElementException(NOT_EXIST_ERROR);
        }
        return new Station(stationEntity.get().getId(), stationEntity.get().getName());
    }

    public void deleteStation(Long id) {
        Optional<StationEntity> wrappedStationEntity = stationDao.findById(id);
        if (wrappedStationEntity.isEmpty()) {
            throw new NoSuchElementException(NOT_EXIST_ERROR);
        }
        stationDao.deleteById(id);
    }
}
