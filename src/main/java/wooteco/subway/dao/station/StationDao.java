package wooteco.subway.dao.station;

import java.util.List;
import wooteco.subway.domain.Station;

public interface StationDao {

    long save(Station station);

    List<Station> findAll();

    Station findById(Long id);

    boolean existByName(String name);

    boolean existById(Long id);

    int delete(Long stationId);
}
