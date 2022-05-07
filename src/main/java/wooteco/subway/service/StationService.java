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

    private static final String DUPLICATE_STATION_NAME = "지하철 이름이 중복될 수 없습니다.";

    private final StationDao dao;

    public StationService(StationDao dao) {
        this.dao = dao;
    }

    public StationResponse save(StationRequest request) {
        String name = request.getName();

        if (dao.isExistName(name)) {
            throw new IllegalArgumentException(DUPLICATE_STATION_NAME);
        }

        return new StationResponse(dao.save(name));
    }

    public List<StationResponse> findAll() {
        List<Station> stations = dao.findAll();
        return stations.stream()
                .map(StationResponse::new)
                .collect(Collectors.toList());
    }

    public void delete(Long id) {
        dao.delete(id);
    }
}
