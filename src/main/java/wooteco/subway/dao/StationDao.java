package wooteco.subway.dao;

import java.util.List;
import wooteco.subway.dao.entity.StationEntity;
import wooteco.subway.domain.Station;

public interface StationDao {

    StationEntity save(Station station);

    List<StationEntity> findAll();

    boolean deleteById(Long id);

    boolean existsByName(String name);

    StationEntity findById(Long id);
}
