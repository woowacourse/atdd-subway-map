package wooteco.subway.dao;

import java.util.List;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Station;

@Repository
public interface StationDao {

    Station save(Station station);

    List<Station> findAll();

    List<Station> findByIds(List<Long> ids);

    int deleteById(Long id);

    boolean exists(Station station);

    boolean exists(Long id);
}
