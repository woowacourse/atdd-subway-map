package wooteco.subway.dao;

import java.util.List;
import wooteco.subway.domain.Station;

public interface StationDao {

    Long save(Station station);

    List<Station> findAll();

    void deleteById(Long stationId);

    Station findById(Long stationId);

    boolean existByName(Station station);
}
