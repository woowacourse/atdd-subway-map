package wooteco.subway.dao;

import java.util.List;
import java.util.Optional;

import wooteco.subway.domain.StationEntity;

public interface StationDao {

    StationEntity save(StationEntity station);

    Optional<StationEntity> findByName(String name);

    Optional<StationEntity> findById(Long id);

    List<StationEntity> findAll();

    int deleteById(Long id);
}
