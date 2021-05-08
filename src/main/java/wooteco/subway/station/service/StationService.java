package wooteco.subway.station.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.exception.station.StationDuplicatedNameException;
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
        validatesNameDuplication(stationRequest);
        Station station = new Station(stationRequest.getName());
        Station newStation = stationDao.save(station);
        log.info(newStation.getName() + "역이 생성되었습니다.");
        return new StationResponse(newStation.getId(), newStation.getName());
    }

    private void validatesNameDuplication(StationRequest stationRequest) {
        stationDao.findByName(stationRequest.getName())
                .ifPresent(l -> {
                    throw new StationDuplicatedNameException();
                });
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
