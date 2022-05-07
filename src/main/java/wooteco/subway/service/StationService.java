package wooteco.subway.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.request.StationRequest;
import wooteco.subway.dto.response.StationResponse;
import wooteco.subway.dao.StationDao;

@Service
public class StationService {

    private static final String STATION_NOT_FOUND_EXCEPTION_MESSAGE = "해당되는 역은 존재하지 않습니다.";
    private static final String NAME_NOT_ALLOWED_EXCEPTION_MESSAGE = "해당 이름의 지하철역을 생성할 수 없습니다.";

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public List<StationResponse> findAll() {
        return stationDao.findAll()
                .stream()
                .map(it -> new StationResponse(it.getId(), it.getName()))
                .sorted(Comparator.comparingLong(StationResponse::getId))
                .collect(Collectors.toList());
    }

    public StationResponse save(StationRequest stationRequest) {
        String stationName = stationRequest.getName();
        validateUniqueName(stationName);

        Station savedStation = stationDao.save(new Station(stationName));
        return new StationResponse(savedStation.getId(), savedStation.getName());
    }

    public void delete(Long id) {
        validateExistingStation(id);
        stationDao.deleteById(id);
    }

    private void validateExistingStation(Long id) {
        boolean isExistingStation = stationDao.findById(id).isPresent();
        if (!isExistingStation) {
            throw new IllegalArgumentException(STATION_NOT_FOUND_EXCEPTION_MESSAGE);
        }
    }

    private void validateUniqueName(String name) {
        boolean isDuplicateName = stationDao.findByName(name).isPresent();
        if (isDuplicateName) {
            throw new IllegalArgumentException(NAME_NOT_ALLOWED_EXCEPTION_MESSAGE);
        }
    }
}
