package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.exception.datanotfound.DataNotFoundException;
import wooteco.subway.exception.datanotfound.StationNotFoundException;
import wooteco.subway.exception.duplicatename.DuplicateNameException;
import wooteco.subway.exception.duplicatename.StationDuplicateException;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(final StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponse createStation(StationRequest stationRequest) {
        validateDuplicate(stationRequest);
        Station station = new Station(stationRequest.getName());
        Station newStation = stationDao.save(station);
        return new StationResponse(newStation.getId(), newStation.getName());
    }

    private void validateDuplicate(StationRequest stationRequest) {
        if (hasDuplicateStation(stationRequest)) {
            throw new StationDuplicateException("이미 등록된 지하철역 이름입니다.");
        }
    }

    private boolean hasDuplicateStation(StationRequest stationRequest) {
        return stationDao.findAll()
                .stream()
                .anyMatch(station -> station.getName().equals(stationRequest.getName()));
    }

    public List<StationResponse> findAll() {
        List<Station> stations = stationDao.findAll();
        return stations.stream()
                .map(station -> new StationResponse(station.getId(), station.getName()))
                .collect(Collectors.toList());
    }

    public int deleteStation(long id) {
        stationDao.findById(id);
        return stationDao.deleteStation(id);
    }
}
