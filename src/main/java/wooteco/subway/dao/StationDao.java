package wooteco.subway.dao;

import java.util.List;
import wooteco.subway.domain.Station;

public interface StationDao {

    Station insert(Station station);

    Boolean existByName(Station station);

    List<Station> findAll();

    void delete(Long id);
}
