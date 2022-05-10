package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

import java.util.List;
import java.util.stream.Collectors;
import wooteco.subway.exception.NotFoundException;

@Service
public class StationService {

    private static final String STATION_NOT_FOUND = "존재하지 않는 지하철역입니다.";
    private static final String DUPLICATE_STATION_NAME = "지하철 이름이 중복될 수 없습니다.";
    private static final String STATION_ALREADY_REGISTERED_IN_SECTION = "해당 지하철역은 노선에 등록되어 있어 삭제할 수 없습니다.";

    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public StationService(StationDao stationDao, SectionDao sectionDao) {
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    @Transactional
    public StationResponse insert(StationRequest request) {
        String name = request.getName();

        if (stationDao.isExistName(name)) {
            throw new IllegalArgumentException(DUPLICATE_STATION_NAME);
        }

        return new StationResponse(stationDao.insert(name));
    }

    @Transactional(readOnly = true)
    public List<StationResponse> findAll() {
        List<Station> stations = stationDao.findAll();
        return stations.stream()
                .map(StationResponse::new)
                .collect(Collectors.toUnmodifiableList());
    }

    @Transactional
    public void delete(Long id) {
        if (sectionDao.existsByStationId(id)) {
            throw new IllegalArgumentException(STATION_ALREADY_REGISTERED_IN_SECTION);
        }
        if (stationDao.delete(id) == 0) {
            throw new NotFoundException(STATION_NOT_FOUND);
        }
    }
}
