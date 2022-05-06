package wooteco.subway.dao;

import java.util.List;
import wooteco.subway.domain.Station;

public interface StationDao {

    Station save(Station station);

    List<Station> findAll();

    int deleteById(Long id);

    boolean exists(Station station);

    boolean exists(Long id);
}
