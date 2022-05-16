package wooteco.subway.dao;

import java.util.List;
import java.util.Optional;
import wooteco.subway.domain.Station;

public interface StationDao {

    Optional<Station> insert(Station station);

    Optional<Station> findById(Long id);

    List<Station> findAll();

    List<Station> findAllByLineId(Long lineId);

    Integer deleteById(Long id);
}
