package wooteco.subway.dao;

import java.util.List;
import java.util.Optional;

import wooteco.subway.entity.StationEntity;

public interface StationDao {
    StationEntity save(StationEntity StationEntity);

    List<StationEntity> findAll();

    boolean deleteById(Long id);

    Optional<StationEntity> findByName(String name);

    Optional<StationEntity> findById(Long id);
}
