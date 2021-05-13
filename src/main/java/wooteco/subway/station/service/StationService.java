package wooteco.subway.station.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.exception.SubwayException;
import wooteco.subway.exception.station.StationDuplicatedNameException;
import wooteco.subway.exception.station.StationNotFoundException;
import wooteco.subway.section.dto.response.SectionResponse;
import wooteco.subway.station.Station;
import wooteco.subway.station.dao.StationDao;
import wooteco.subway.station.dto.StationRequest;
import wooteco.subway.station.dto.StationResponse;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
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
        log.info("{} 이 생성되었습니다.", newStation.getName());
        return new StationResponse(newStation.getId(), newStation.getName());
    }

    private void validatesNameDuplication(StationRequest stationRequest) {
        boolean isExist = stationDao.existByName(stationRequest.getName());
        if (isExist) {
            throw new StationDuplicatedNameException();
        }
    }

    public List<StationResponse> findAll() {
        List<Station> stations = stationDao.findAll();
        log.info("등록된 지하철 역 조회 성공");
        return stations.stream()
                .map(station -> new StationResponse(station.getId(), station.getName()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void delete(Long id) {
        stationDao.delete(id);
        log.info("지하철 역 삭제 성공");
    }

    public void checkValidStation(Long upStationId, Long downStationId) {
        validatesSameStation(upStationId, downStationId);
        validatesExistStation(upStationId);
        validatesExistStation(downStationId);
    }

    private void validatesSameStation(Long upStationId, Long downStationId) {
        if (upStationId.equals(downStationId)) {
            throw new SubwayException("같은 역을 등록할 수 없습니다.");
        }
    }

    private void validatesExistStation(Long id) {
        boolean isExist = stationDao.existById(id);
        if (!isExist) {
            throw new StationNotFoundException();
        }
    }

    public List<StationResponse> findStations(List<SectionResponse> sections) {
        Set<Long> sortedStations = new LinkedHashSet<>();
        for (SectionResponse section : sections) {
            sortedStations.add(section.getUpStationId());
            sortedStations.add(section.getDownStationId());
        }
        return sortedStations.stream()
                .map(stationDao::findById)
                .map(StationResponse::new)
                .collect(Collectors.toList());
    }

    public StationResponse findById(Long id) {
        Station station = stationDao.findById(id);
        return new StationResponse(station);
    }
}
