package wooteco.subway.dao;

import java.util.List;
import wooteco.subway.domain.Station;

public interface StationDao {
    Station save(String name);

    void deleteAll();

    List<Station> findAll();

    void deleteById(Long id);
}
