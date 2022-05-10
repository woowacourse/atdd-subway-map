package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.info.StationInfo;

@Service
public class StationService {
    private static final String ERROR_MESSAGE_DUPLICATE_NAME = "중복된 지하철 역 이름입니다.";
    private static final String ERROR_MESSAGE_NOT_EXISTS_ID = "존재하지 않는 지하철 역입니다.";

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    @Transactional
    public StationInfo save(StationInfo stationInfo) {
        if (stationDao.existByName(stationInfo.getName())) {
            throw new IllegalArgumentException(ERROR_MESSAGE_DUPLICATE_NAME);
        }
        Station station = new Station(stationInfo.getName());
        Station newStation = stationDao.save(station);
        return new StationInfo(newStation.getId(), newStation.getName());
    }

    public List<StationInfo> findAll() {
        List<Station> stations = stationDao.findAll();
        return stations.stream()
            .map(it -> new StationInfo(it.getId(), it.getName()))
            .collect(Collectors.toList());
    }

    public void delete(Long id) {
        if (!stationDao.existById(id)) {
            throw new IllegalArgumentException(ERROR_MESSAGE_NOT_EXISTS_ID);
        }
        stationDao.delete(id);
    }
}
