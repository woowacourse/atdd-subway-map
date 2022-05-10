package wooteco.subway.dao;

import java.util.List;
import wooteco.subway.domain.Station;

public interface StationDao {

    Long save(Station station);

    Station findById(Long id);

    List<Station> findAll();

    boolean hasStation(String name);

    void deleteById(Long id);
}
