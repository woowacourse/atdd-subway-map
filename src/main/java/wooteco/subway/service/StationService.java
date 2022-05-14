package wooteco.subway.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.RegisteredStationDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.dto.request.StationRequest;
import wooteco.subway.dto.response.StationResponse;
import wooteco.subway.entity.StationEntity;
import wooteco.subway.exception.NotFoundException;

@Service
public class StationService {

    private static final String STATION_NOT_FOUND_EXCEPTION_MESSAGE = "해당되는 역은 존재하지 않습니다.";
    private static final String NAME_NOT_ALLOWED_EXCEPTION_MESSAGE = "해당 이름의 지하철역을 생성할 수 없습니다.";
    private static final String REGISTERED_STATION_EXCEPTION_MESSAGE = "노선에 등록된 역은 제거할 수 없습니다.";

    private final StationDao stationDao;
    private final RegisteredStationDao registeredStationDao;

    public StationService(StationDao stationDao, RegisteredStationDao registeredStationDao) {
        this.stationDao = stationDao;
        this.registeredStationDao = registeredStationDao;
    }

    public List<StationResponse> findAll() {
        return stationDao.findAll()
                .stream()
                .map(it -> new StationResponse(it.getId(), it.getName()))
                .sorted(Comparator.comparingLong(StationResponse::getId))
                .collect(Collectors.toList());
    }

    @Transactional
    public StationResponse save(StationRequest stationRequest) {
        String stationName = stationRequest.getName();
        validateUniqueName(stationName);

        StationEntity savedStation = stationDao.save(new StationEntity(stationName));
        return new StationResponse(savedStation.getId(), savedStation.getName());
    }

    @Transactional
    public void delete(Long id) {
        validateExistingStation(id);
        validateUnRegisteredStation(id);
        stationDao.deleteById(id);
    }

    private void validateUniqueName(String name) {
        boolean isDuplicateName = stationDao.findByName(name).isPresent();
        if (isDuplicateName) {
            throw new IllegalArgumentException(NAME_NOT_ALLOWED_EXCEPTION_MESSAGE);
        }
    }

    private void validateExistingStation(Long id) {
        boolean isExistingStation = stationDao.findById(id).isPresent();
        if (!isExistingStation) {
            throw new NotFoundException(STATION_NOT_FOUND_EXCEPTION_MESSAGE);
        }
    }

    private void validateUnRegisteredStation(Long id) {
        boolean isUnRegistered = registeredStationDao.findAllByStationId(id).isEmpty();
        if (!isUnRegistered) {
            throw new IllegalArgumentException(REGISTERED_STATION_EXCEPTION_MESSAGE);
        }
    }
}
