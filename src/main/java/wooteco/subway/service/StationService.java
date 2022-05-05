package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.StationDaoImpl;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;

@Service
public class StationService {

    private final StationDaoImpl stationDaoImpl;

    public StationService(StationDaoImpl stationDaoImpl) {
        this.stationDaoImpl = stationDaoImpl;
    }

    public Station create(StationRequest stationRequest) {
        final Station station = stationRequest.toEntity();
        return stationDaoImpl.save(station);
    }

    public List<Station> show() {
        return stationDaoImpl.findAll();
    }

    public void delete(Long id) {
        stationDaoImpl.deleteById(id);
    }
}
