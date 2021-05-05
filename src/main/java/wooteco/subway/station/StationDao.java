package wooteco.subway.station;

import wooteco.subway.line.LineColor;
import wooteco.subway.line.LineName;

import java.util.List;

public interface StationDao {
    Station save(Station station);

    List<Station> findAll();

    Station findById(Long id);

    boolean checkExistName(StationName name);

    boolean checkExistId(Long id);

    void delete(Station station);

    void deleteAll();
}
