package wooteco.subway.dao.station;

import java.util.List;
import wooteco.subway.domain.station.Station;

public interface StationDao {

    Station save(Station station);

    List<Station> findAll();

    void deleteById(Long id);

    Station findById(Long id);
}
