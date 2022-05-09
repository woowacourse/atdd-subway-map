package wooteco.subway.dao;

import java.util.List;
import wooteco.subway.domain.Station;

public interface CommonStationDao {

    Station save(final Station station);

    List<Station> findAll();

    void deleteById(final Long id);
}
