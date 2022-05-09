package wooteco.subway.dao;

import java.util.List;
import wooteco.subway.domain.Station;
import wooteco.subway.entity.StationEntity;

public interface StationDao {

    StationEntity save(Station station);

    List<StationEntity> findAll();

    boolean deleteById(Long id);

    boolean existsByName(String name);
}
