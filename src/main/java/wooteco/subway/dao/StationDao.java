package wooteco.subway.dao;

import java.util.List;
import java.util.Optional;

import wooteco.subway.entity.StationEntity;

public interface StationDao {
    Long save(StationEntity StationEntity);

    List<StationEntity> findAll();

    boolean deleteById(Long id);

    Optional<StationEntity> findById(Long id);
}
