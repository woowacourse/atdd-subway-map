package wooteco.subway.station;

import java.util.List;
import java.util.Optional;

public interface StationDao {

    Station save(Station station);

    List<Station> findAll();

    Optional<Station> findStationByName(String name);
}
