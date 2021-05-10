package wooteco.subway.station.dao;

import java.util.List;
import wooteco.subway.station.Station;

public interface StationDao {

    Station save(Station station);

    int countByName(String name);

    List<Station> showAll();

    int delete(long id);
}
