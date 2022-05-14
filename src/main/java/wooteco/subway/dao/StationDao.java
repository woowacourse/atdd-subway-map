package wooteco.subway.dao;

import java.util.List;
import wooteco.subway.domain.Station;

public interface StationDao {
    Station save(Station station);

    List<Station> findAll();

    Station findById(Long id);

    int deleteById(Long id);
}
