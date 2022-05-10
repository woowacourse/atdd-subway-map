package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationService {
    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponse save(StationRequest stationRequest) {
        Station station = validateAndSave(stationRequest.toStation());
        return new StationResponse(station.getId(), station.getName());
    }

    private Station validateAndSave(Station station) {
        checkDuplication(station);
        return stationDao.save(station);
    }

    private void checkDuplication(Station station) {
        if (stationDao.getStationsHavingName(station.getName()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 역 이름입니다.");
        }
    }

    public List<StationResponse> findAll(){
        List<Station> stations = stationDao.findAll();
        return stations.stream()
                .map(station -> new StationResponse(station.getId(), station.getName()))
                .collect(Collectors.toList());
    }

    public void delete(Long id) {
        stationDao.deleteById(id);
    }
}
