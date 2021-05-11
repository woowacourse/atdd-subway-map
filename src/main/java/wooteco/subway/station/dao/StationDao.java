package wooteco.subway.station.dao;

import java.util.List;
import wooteco.subway.station.Station;

public interface StationDao {

    Station save(Station station);

    List<Station> findAll();

    void deleteById(Long id);

    Station findById(Long id);

    List<Station> findAllByIds(List<Long> ids);
}
