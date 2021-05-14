package wooteco.subway.repository;

import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import wooteco.subway.dao.station.StationDao;
import wooteco.subway.domain.Station;

@Repository
@AllArgsConstructor
public class StationRepository {

    private final StationDao stationDao;


    public Optional<Station> findStationById(Long upStationId) {
        return stationDao.findStationById(upStationId);
    }
}
