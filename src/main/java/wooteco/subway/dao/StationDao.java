package wooteco.subway.dao;

import java.util.List;
import wooteco.subway.domain.Station;

public interface StationDao {
    Station save(Station station);

    Station findById(Long id);

    List<Station> findAll();

    int delete(Long id);

    List<Station> findByIds(List<Long> ids);
}
