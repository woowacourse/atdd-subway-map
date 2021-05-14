package wooteco.subway.station.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import wooteco.subway.exception.DuplicatedNameException;
import wooteco.subway.exception.NotFoundException;
import wooteco.subway.section.dto.SectionResponse;
import wooteco.subway.station.Station;
import wooteco.subway.station.dto.StationRequest;
import wooteco.subway.station.dto.StationResponse;
import wooteco.subway.station.repository.JdbcStationDao;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationService {
    private static final Logger log = LoggerFactory.getLogger(StationService.class);
    private static final int VALID_STATION_SIZE = 2;

    private JdbcStationDao stationRepository;

    public StationService(JdbcStationDao stationRepository) {
        this.stationRepository = stationRepository;
    }

    public StationResponse save(StationRequest stationRequest) {
        validateStationName(stationRequest);
        Station station = stationRequest.toEntity();
        Station newStation = stationRepository.save(station);
        log.info("{} 생성 성공.", newStation.getName());
        return new StationResponse(newStation);
    }

    private void validateStationName(StationRequest stationRequest) {
        if (checkNameDuplicate(stationRequest)) {
            throw new DuplicatedNameException("중복된 이름의 역이 존재합니다.");
        }
    }

    private boolean checkNameDuplicate(StationRequest stationRequest) {
        return stationRepository.findByName(stationRequest.getName());
    }

    public List<StationResponse> findAllStations() {
        List<Station> stations = stationRepository.findAll();
        log.info("등록된 지하철 역 조회 성공");
        return stations.stream()
                .map(station -> new StationResponse(station.getId(), station.getName()))
                .collect(Collectors.toList());
    }

    public StationResponse findBy(Long id) {
        Station station = stationRepository.findBy(id);
        return new StationResponse(station);
    }

    public void delete(Long id) {
        stationRepository.deleteById(id);
        log.info("지하철 역 삭제 성공");
    }

    public void validateStations(Long upStationId, Long downStationId) {
        List<Station> stations = stationRepository.findTwoStations(upStationId, downStationId);
        if (stations.size() != VALID_STATION_SIZE) {
            throw new NotFoundException("등록되지 않은 역은 상행 혹은 하행역으로 추가할 수 없습니다.");
        }
    }

    public List<StationResponse> getStations(SectionResponse sectionRes) {
        StationResponse up = findBy(sectionRes.getUpStationId());
        StationResponse down = findBy(sectionRes.getDownStationId());
        return Arrays.asList(up, down);
    }

    public List<StationResponse> findStationsByIds(List<Long> stationIds) {
        return stationIds.stream()
                .map(id -> stationRepository.findBy(id))
                .map(StationResponse::new)
                .collect(Collectors.toList());
    }
}
