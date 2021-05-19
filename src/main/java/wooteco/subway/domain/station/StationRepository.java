package wooteco.subway.domain.station;

import java.util.List;

public interface StationRepository {
    Station save(Station station);
    Station findById(Long id);
    List<Station> findAll();
    int delete(Long id);
    boolean contains(Long id);
}
