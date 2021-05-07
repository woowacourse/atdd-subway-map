package wooteco.subway.dao.station;

import java.util.List;
import wooteco.subway.domain.Station;

public interface StationDao {

    Station save(Station station);

    List<Station> showAll();

    int delete(long id);
}
