package wooteco.subway.dao;

import java.util.List;
import wooteco.subway.domain.Station;

public interface StationDao {

    void deleteAll();

    List<Station> findAll();

    void deleteById(Long id);

    Station save(Station station);
}
