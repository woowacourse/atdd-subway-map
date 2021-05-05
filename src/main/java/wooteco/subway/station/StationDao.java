package wooteco.subway.station;

import java.util.List;

public interface StationDao {
    Station save(Station station);

    List<Station> findAll();

    Station findById(Long id);

    void delete(Station station);

    void deleteAll();
}
