package wooteco.subway.station.domain;

import java.util.List;

public interface StationRepository {
    Station save(Station station);
    List<Station> findAll();
}
