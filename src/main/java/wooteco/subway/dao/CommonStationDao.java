package wooteco.subway.dao;

import java.util.List;
import wooteco.subway.domain.Station;

public interface CommonStationDao {

    Station save(final Station station);

    List<Station> findAll();

    Station findById(final Long id);

    int deleteById(final Long id);
}
