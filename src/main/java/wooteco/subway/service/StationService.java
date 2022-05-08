package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.utils.StringFormat;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponse save(StationRequest stationRequest) {
        if (stationDao.findByName(stationRequest.getName()).isPresent()) {
            throw new IllegalArgumentException(
                    StringFormat.errorMessage(stationRequest.getName(), "중복되는 지하철역이 존재합니다."));
        }
        Station station = stationRequest.toEntity();
        Station newStation = stationDao.save(station);
        return StationResponse.of(newStation);
    }

    public List<StationResponse> findAll() {
        List<Station> stations = stationDao.findAll();
        return stations.stream()
                .map(StationResponse::of)
                .collect(Collectors.toUnmodifiableList());
    }

    public Void delete(Long id) {
        Station station = stationDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        StringFormat.errorMessage(id, "해당 ID의 지하철역이 존재하지 않습니다.")));
        stationDao.delete(station);
        return null;
    }
}
