package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.station.StationRequest;
import wooteco.subway.dto.station.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class StationService {

    private static final String DUPLICATE_STATION_NAME = "지하철 이름이 중복될 수 없습니다.";
    private static final String CAN_NOT_DELETE = "구간에 존재하는 역은 삭제할 수 없습니다.";

    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public StationService(StationDao stationDao, SectionDao sectionDao) {
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    public StationResponse save(StationRequest request) {
        String name = request.getName();

        if (stationDao.isExistName(name)) {
            throw new IllegalArgumentException(DUPLICATE_STATION_NAME);
        }

        return new StationResponse(stationDao.save(name));
    }

    public List<StationResponse> findAll() {
        List<Station> stations = stationDao.findAll();
        return stations.stream()
                .map(StationResponse::new)
                .collect(Collectors.toList());
    }

    public void delete(Long id) {
        if (sectionDao.isStationExist(id)) {
            throw new IllegalArgumentException(CAN_NOT_DELETE);
        }
        stationDao.delete(id);
    }
}
