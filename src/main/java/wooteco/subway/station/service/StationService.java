package wooteco.subway.station.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.station.Station;
import wooteco.subway.station.dao.StationDao;
import wooteco.subway.station.dto.StationRequest;
import wooteco.subway.station.dto.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationService {
    private static final Logger log = LoggerFactory.getLogger(StationService.class);

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    @Transactional
    public StationResponse save(StationRequest stationRequest) {
        validateStationName(stationRequest);
        Station station = new Station(stationRequest.getName());
        Station newStation = stationDao.save(station);
        log.info(newStation.getName() + "역이 생성되었습니다.");
        return new StationResponse(newStation.getId(), newStation.getName());
    }

    private void validateStationName(StationRequest stationRequest) {
        if (checkNameDuplicate(stationRequest)) {
            throw new IllegalArgumentException("중복된 이름의 역이 존재합니다.");
        }
    }

    private boolean checkNameDuplicate(StationRequest stationRequest) {
        return stationDao.findAll().stream()
                .anyMatch(station -> station.isSameName(stationRequest.getName()));
    }

    public List<StationResponse> findAll() {
        List<Station> stations = stationDao.findAll();
        log.info("등록된 지하철 역 조회 성공");
        return stations.stream()
                .map(station -> new StationResponse(station.getId(), station.getName()))
                .collect(Collectors.toList());
    }

    public void delete(Long id) {
        stationDao.delete(id);
        log.info("지하철 역 삭제 성공");
    }
}
