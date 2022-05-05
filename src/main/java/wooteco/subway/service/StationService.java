package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.StationDaoImpl;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.exception.StationNotFoundException;

@Service
public class StationService {

    private final StationDaoImpl stationDaoImpl;

    public StationService(final StationDaoImpl stationDaoImpl) {
        this.stationDaoImpl = stationDaoImpl;
    }

    public Station create(final StationRequest stationRequest) {
        final Station station = stationRequest.toEntity();
        return stationDaoImpl.save(station);
    }

    public List<Station> show() {
        return stationDaoImpl.findAll();
    }

    public void delete(final Long id) {
        final Station targetLine = stationDaoImpl.findById(id)
            .orElseThrow(() -> new StationNotFoundException("이미 존재하는 지하철역 이름입니다.", 1));
        stationDaoImpl.delete(targetLine);
    }
}
