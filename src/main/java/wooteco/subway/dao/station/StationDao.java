package wooteco.subway.dao.station;

import java.util.List;
import wooteco.subway.domain.Station;

public interface StationDao {

    long save(Station station);

    boolean existStationById(Long id);

    boolean existStationByName(String name);

    List<Station> findAll();

    void delete(Long id);
}
