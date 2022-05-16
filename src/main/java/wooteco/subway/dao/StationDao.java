package wooteco.subway.dao;

import java.util.List;
import wooteco.subway.domain.Station;

public interface StationDao {

    Station save(Station station);

    List<Station> findAll();

    boolean deleteById(Long id);

    boolean existsByName(String name);

    Station findById(Long id);
}
