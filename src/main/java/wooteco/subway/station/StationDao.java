package wooteco.subway.station;

import java.util.List;

public interface StationDao {
    Station save(Station station);

    List<Station> findAll();

    void delete(Long id);
}
