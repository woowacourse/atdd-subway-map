package wooteco.subway.repository.dao;

import java.util.List;
import java.util.Optional;
import wooteco.subway.repository.entity.StationEntity;

public interface StationDao {

    StationEntity save(StationEntity entity);

    List<StationEntity> findAll();

    Integer deleteById(Long id);

    List<StationEntity> findByIds(List<Long> ids);

    Optional<StationEntity> findById(Long id);
}
